package com.scene.mesh.engin.generator;

import com.scene.mesh.model.event.Event;
import org.apache.flink.cep.configuration.ObjectConfiguration;
import org.apache.flink.cep.dynamic.GroupPatternWrapper;
import org.apache.flink.cep.dynamic.PatternWrapper;
import org.apache.flink.cep.dynamic.condition.AviatorCondition;
import org.apache.flink.cep.dynamic.impl.json.spec.GraphSpec;
import org.apache.flink.cep.dynamic.impl.json.util.CepJsonUtils;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.GroupPattern;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.Quantifier;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;

/**
 * 规则生成
 */
public class PatternRuleGenerator {
    public static void main(String[] args) throws Exception {

        // 或 模式测试
//        generateOrPattern();

//        // 原有的Pattern示例
//        generateSimplePattern();
//
//        System.out.println("\n" + "=".repeat(80) + "\n");
//
//        // 新增的GroupPattern示例
//        generateGroupPattern();
//        generateNestedGroupPattern();
//        generateNestedGroupPatternWithName();
        json2Pattern();
    }

    private static void generateOrPattern() throws JsonProcessingException {
        System.out.println("=== 或Pattern示例 ===");

        Pattern<Event,?> pattern = Pattern.<Event>begin("start",AfterMatchSkipStrategy.skipPastLastEvent())
                .where(new AviatorCondition<>("metaEventId == 'wakeup_word' && payload.word == '能'"))
                .optional()
                .followedBy("followed")
                .where(new AviatorCondition<>("metaEventId == 'wakeup_word' && payload.word == '能'"))
                .optional()
                .followedBy("followed2")
                .where(new AviatorCondition<>("metaEventId == 'wakeup_word' && payload.word == '不能'"))
                .optional()
                .within(Time.seconds(10));

        System.out.println(CepJsonUtils.convertPatternToJSONString(pattern));
    }

    /**
     * 简单Pattern示例：抢购行为检测
     */
    public static void generateSimplePattern() throws Exception {
        System.out.println("=== 简单Pattern示例：抢购行为检测 ===");
        
        Pattern<Event, Event> pattern = Pattern
                // 1. 起点：高频刷新商品页
                .<Event>begin("rapid-probing")
                .where(new AviatorCondition<>(
                        "event.eventType == 'view_product' && event.properties.productId == 'limited_sneaker_2025'"
                ))
                .or(new AviatorCondition<>(
                        "event.eventType == 'check_stock_api' && event.properties.productId == 'limited_sneaker_2025'"
                ))
                // 【次数】量词：要求发生5次或更多次
                .timesOrMore(5)
                // 【连续性】策略：要求这些探测行为必须是连续发生的
                .consecutive()
                // 为这个连续探测行为设置一个10秒的内部时间窗口
                .within(Time.seconds(10))

                // 2. 严格邻接：在高频探测之后，下一个事件必须是"加入购物车"
                .next("add-to-cart")
                .where(new AviatorCondition<>(
                        "event.eventType == 'add_to_cart' && event.properties.productId == 'limited_sneaker_2025'"
                ))

                // 3. 非严格邻接 + 可选：我们定义一个"人机验证成功"的事件，并将其设为"可选"
                .followedBy("human-verification")
                .where(new AviatorCondition<>("event.eventType == 'captcha_verified'"))
                // 【可选】算子：这个事件可以发生，也可以不发生
                .optional()

                // 4. 非严格邻接：在上述步骤之后，最终会尝试支付
                .followedBy("checkout-attempt")
                .where(new AviatorCondition<>("event.eventType == 'start_checkout'"))

                // 5. 全局时间约束：整个序列必须在30秒内完成
                .within(Time.seconds(30));

        System.out.println(CepJsonUtils.convertPatternToJSONString(pattern));
    }
    
    /**
     * 复杂Pattern示例：复杂的用户风险行为检测组合
     */
    public static void generateGroupPattern() throws Exception {
        System.out.println("=== 复杂Pattern示例：复杂用户风险行为检测 ===");
        
        // 子模式1：抢购行为模式
        Pattern<Event, Event> scalpingPattern = Pattern
                .<Event>begin("scalping-probe")
                .where(new AviatorCondition<>(
                        "event.eventType == 'view_product' && event.properties.productId == 'limited_item'"
                ))
                .timesOrMore(3)
                .consecutive()
                .within(Time.seconds(5))
                .next("scalping-cart")
                .where(new AviatorCondition<>(
                        "event.eventType == 'add_to_cart' && event.properties.quantity > 5"
                ));

        // 子模式2：账户异常行为模式
        Pattern<Event, Event> accountAbusePattern = Pattern
                .<Event>begin("account-switch")
                .where(new AviatorCondition<>(
                        "event.eventType == 'login' && event.properties.isNewDevice == true"
                ))
                .followedBy("rapid-profile-change")
                .where(new AviatorCondition<>(
                        "event.eventType == 'profile_update' && (event.properties.field == 'address' || event.properties.field == 'payment_method')"
                ))
                .times(2, 5);

        // 子模式3：支付风险行为模式  
        Pattern<Event, Event> paymentRiskPattern = Pattern
                .<Event>begin("payment-attempts")
                .where(new AviatorCondition<>(
                        "event.eventType == 'payment_failed' && event.properties.reason == 'card_declined'"
                ))
                .timesOrMore(2)
                .followedBy("payment-method-change")
                .where(new AviatorCondition<>(
                        "event.eventType == 'payment_method_updated'"
                ))
                .followedBy("successful-payment")
                .where(new AviatorCondition<>(
                        "event.eventType == 'payment_success' && event.properties.amount > 1000"
                ));

        // 使用Pattern将上述三个子模式组合成一个复杂的风险检测模式
        // 场景：检测可能的欺诈用户行为组合
        Pattern<Event, Event> riskUserGroupPattern = Pattern
                // 开始组合：检测抢购行为
                .begin(scalpingPattern)
                .optional()
                // 在抢购行为后的1小时内，检测账户异常行为
                .followedBy(accountAbusePattern)
                .within(Time.hours(1))
                .optional()
                // 在账户异常行为后的30分钟内，检测支付风险行为
                .followedBy(paymentRiskPattern)
                .within(Time.minutes(30))
                .optional()
                .followedBy("successful-refund")
                .where(new AviatorCondition<>("event.eventType == 'refund_success'"))
                // 整个组合模式在2小时内完成
                .within(Time.hours(2));

        // 为组合模式设置量词：可以检测这种复合行为发生1-3次
        Pattern<Event, Event> finalRiskPattern = riskUserGroupPattern
                .times(1, 3);

        System.out.println("复杂Pattern JSON:");
        System.out.println(CepJsonUtils.convertPatternToJSONString(finalRiskPattern));
    }
    
    /**
     * 嵌套Pattern示例：多层次的业务流程检测
     */
    public static void generateNestedGroupPattern() throws Exception {
        System.out.println("\n=== 嵌套Pattern示例：电商完整交易流程检测 ===");
        
        // Pattern1：用户浏览行为模式
        Pattern<Event, Event> browsingGroup = Pattern
                .<Event>begin("product-search")
                .where(new AviatorCondition<>("event.eventType == 'search' && event.properties.category == 'electronics'"))
                .followedBy("product-view")
                .where(new AviatorCondition<>("event.eventType == 'view_product'"))
                .timesOrMore(3)
                .followedBy("compare-products")
                .where(new AviatorCondition<>("event.eventType == 'compare' && event.properties.productCount >= 2"))
                .optional();
        
        // Pattern2：购物车行为模式
        Pattern<Event, Event> cartGroup = Pattern
                .<Event>begin("add-items")
                .where(new AviatorCondition<>("event.eventType == 'add_to_cart'"))
                .followedBy("modify-cart")
                .where(new AviatorCondition<>(
                        "event.eventType == 'cart_update' && (event.properties.action == 'quantity_change' || event.properties.action == 'remove_item')"
                ))
                .optional()
                .followedBy("cart-review")
                .where(new AviatorCondition<>("event.eventType == 'view_cart'"));
        
        // Pattern3：结账行为模式
        Pattern<Event, Event> checkoutGroup = Pattern
                .<Event>begin("start-checkout")
                .where(new AviatorCondition<>("event.eventType == 'start_checkout'"))
                .followedBy("shipping-selection")
                .where(new AviatorCondition<>("event.eventType == 'select_shipping'"))
                .followedBy("payment-processing")
                .where(new AviatorCondition<>("event.eventType == 'process_payment'"))
                .followedBy("order-confirmation")
                .where(new AviatorCondition<>("event.eventType == 'order_confirmed'"));
        
        // 第一层Pattern组合：购物流程组合
        Pattern<Event, Event> shoppingFlowGroup = Pattern
                .begin(browsingGroup)
                .followedBy(cartGroup)
                .within(Time.minutes(30))
                .followedBy(checkoutGroup)
                .within(Time.minutes(15));
        
        // Pattern4：售后服务行为模式
        Pattern<Event, Event> afterSaleGroup = Pattern
                .<Event>begin("order-tracking")
                .where(new AviatorCondition<>("event.eventType == 'track_order'"))
                .followedBy("delivery-received")
                .where(new AviatorCondition<>("event.eventType == 'delivery_confirmed'"))
                .followedBy("post-purchase")
                .where(new AviatorCondition<>(
                        "event.eventType == 'review_product' || event.eventType == 'customer_service_contact'"
                ))
                .optional();
        
        // 最终的嵌套Pattern：完整的电商用户旅程 - 购物流程组合+售后服务行为模式
        Pattern<Event, Event> completeUserJourneyPattern = Pattern
                .begin(shoppingFlowGroup)
                .followedBy(afterSaleGroup)
                .within(Time.days(7)) // 整个用户旅程在7天内完成
                // 可以检测这种完整旅程重复发生的情况（回头客分析）
                .times(1, 5);
        
        System.out.println("嵌套Pattern JSON:");
        System.out.println(CepJsonUtils.convertPatternToJSONString(completeUserJourneyPattern));
    }

    public static void generateNestedGroupPatternWithName() throws Exception {
        System.out.println("\n=== 嵌套Pattern示例：电商完整交易流程检测 ===");

        // Pattern1：用户浏览行为模式
        Pattern<Event, Event> browsingGroup = Pattern
                .<Event>begin("product-search")
                .where(new AviatorCondition<>("event.eventType == 'search' && event.properties.category == 'electronics'"))
                .followedBy("product-view")
                .where(new AviatorCondition<>("event.eventType == 'view_product'"))
                .timesOrMore(3)
                .followedBy("compare-products")
                .where(new AviatorCondition<>("event.eventType == 'compare' && event.properties.productCount >= 2"))
                .optional();

        // Pattern2：购物车行为模式
        Pattern<Event, Event> cartGroup = Pattern
                .<Event>begin("add-items")
                .where(new AviatorCondition<>("event.eventType == 'add_to_cart'"))
                .followedBy("modify-cart")
                .where(new AviatorCondition<>(
                        "event.eventType == 'cart_update' && (event.properties.action == 'quantity_change' || event.properties.action == 'remove_item')"
                ))
                .optional()
                .followedBy("cart-review")
                .where(new AviatorCondition<>("event.eventType == 'view_cart'"));

        // Pattern3：结账行为模式
        Pattern<Event, Event> checkoutGroup = Pattern
                .<Event>begin("start-checkout")
                .where(new AviatorCondition<>("event.eventType == 'start_checkout'"))
                .followedBy("shipping-selection")
                .where(new AviatorCondition<>("event.eventType == 'select_shipping'"))
                .followedBy("payment-processing")
                .where(new AviatorCondition<>("event.eventType == 'process_payment'"))
                .followedBy("order-confirmation")
                .where(new AviatorCondition<>("event.eventType == 'order_confirmed'"));

        // 第一层Pattern组合：购物流程组合
        Pattern<Event, Event> shoppingFlowGroup = GroupPatternWrapper
                .begin("browsingGroup",browsingGroup)
                .followedBy("cartGroup",cartGroup)
//                .within(Time.minutes(30))
                .followedBy("checkoutGroup",checkoutGroup)
                .within(Time.minutes(15));

        // Pattern4：售后服务行为模式
        Pattern<Event, Event> afterSaleGroup = Pattern
                .<Event>begin("order-tracking")
                .where(new AviatorCondition<>("event.eventType == 'track_order'"))
                .followedBy("delivery-received")
                .where(new AviatorCondition<>("event.eventType == 'delivery_confirmed'"))
                .followedBy("post-purchase")
                .where(new AviatorCondition<>(
                        "event.eventType == 'review_product' || event.eventType == 'customer_service_contact'"
                ))
                .optional();

        // 最终的嵌套Pattern：完整的电商用户旅程 - 购物流程组合+售后服务行为模式
        Pattern<Event, Event> completeUserJourneyPattern = GroupPatternWrapper
                .begin("shoppingFlowGroup",shoppingFlowGroup)
                .followedBy("afterSaleGroup",afterSaleGroup)
                .within(Time.days(7)) // 整个用户旅程在7天内完成
                // 可以检测这种完整旅程重复发生的情况（回头客分析）
                .times(1, 5);

        System.out.println("嵌套Pattern JSON:");
        System.out.println(CepJsonUtils.convertPatternToJSONString(completeUserJourneyPattern));
    }

    private static void json2Pattern() throws Exception {
        String json = "{\n" +
                "  \"nodes\": [\n" +
                "    {\n" +
                "      \"name\": \"afterSaleGroup\",\n" +
                "      \"quantifier\": {\n" +
                "        \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "        \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "        \"properties\": [\n" +
                "          \"TIMES\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"condition\": null,\n" +
                "      \"graph\": {\n" +
                "        \"nodes\": [\n" +
                "          {\n" +
                "            \"name\": \"post-purchase\",\n" +
                "            \"quantifier\": {\n" +
                "              \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"properties\": [\n" +
                "                \"SINGLE\",\n" +
                "                \"OPTIONAL\"\n" +
                "              ]\n" +
                "            },\n" +
                "            \"condition\": {\n" +
                "              \"expression\": \"event.eventType == 'review_product' || event.eventType == 'customer_service_contact'\",\n" +
                "              \"type\": \"AVIATOR\"\n" +
                "            },\n" +
                "            \"times\": null,\n" +
                "            \"untilCondition\": null,\n" +
                "            \"window\": null,\n" +
                "            \"afterMatchSkipStrategy\": {\n" +
                "              \"type\": \"NO_SKIP\",\n" +
                "              \"patternName\": null\n" +
                "            },\n" +
                "            \"type\": \"ATOMIC\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"delivery-received\",\n" +
                "            \"quantifier\": {\n" +
                "              \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"properties\": [\n" +
                "                \"SINGLE\"\n" +
                "              ]\n" +
                "            },\n" +
                "            \"condition\": {\n" +
                "              \"expression\": \"event.eventType == 'delivery_confirmed'\",\n" +
                "              \"type\": \"AVIATOR\"\n" +
                "            },\n" +
                "            \"times\": null,\n" +
                "            \"untilCondition\": null,\n" +
                "            \"window\": null,\n" +
                "            \"afterMatchSkipStrategy\": {\n" +
                "              \"type\": \"NO_SKIP\",\n" +
                "              \"patternName\": null\n" +
                "            },\n" +
                "            \"type\": \"ATOMIC\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"order-tracking\",\n" +
                "            \"quantifier\": {\n" +
                "              \"consumingStrategy\": \"STRICT\",\n" +
                "              \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"properties\": [\n" +
                "                \"SINGLE\"\n" +
                "              ]\n" +
                "            },\n" +
                "            \"condition\": {\n" +
                "              \"expression\": \"event.eventType == 'track_order'\",\n" +
                "              \"type\": \"AVIATOR\"\n" +
                "            },\n" +
                "            \"times\": null,\n" +
                "            \"untilCondition\": null,\n" +
                "            \"window\": null,\n" +
                "            \"afterMatchSkipStrategy\": {\n" +
                "              \"type\": \"NO_SKIP\",\n" +
                "              \"patternName\": null\n" +
                "            },\n" +
                "            \"type\": \"ATOMIC\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"edges\": [\n" +
                "          {\n" +
                "            \"source\": \"delivery-received\",\n" +
                "            \"target\": \"post-purchase\",\n" +
                "            \"type\": \"SKIP_TILL_NEXT\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"source\": \"order-tracking\",\n" +
                "            \"target\": \"delivery-received\",\n" +
                "            \"type\": \"SKIP_TILL_NEXT\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"times\": {\n" +
                "        \"from\": 1,\n" +
                "        \"to\": 5,\n" +
                "        \"windowTime\": null\n" +
                "      },\n" +
                "      \"untilCondition\": null,\n" +
                "      \"window\": {\n" +
                "        \"type\": \"FIRST_AND_LAST\",\n" +
                "        \"time\": {\n" +
                "          \"unit\": \"DAYS\",\n" +
                "          \"size\": 7\n" +
                "        }\n" +
                "      },\n" +
                "      \"afterMatchSkipStrategy\": {\n" +
                "        \"type\": \"NO_SKIP\",\n" +
                "        \"patternName\": null\n" +
                "      },\n" +
                "      \"type\": \"COMPOSITE\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"shoppingFlowGroup\",\n" +
                "      \"quantifier\": {\n" +
                "        \"consumingStrategy\": \"STRICT\",\n" +
                "        \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "        \"properties\": [\n" +
                "          \"SINGLE\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"condition\": null,\n" +
                "      \"graph\": {\n" +
                "        \"nodes\": [\n" +
                "          {\n" +
                "            \"name\": \"checkoutGroup\",\n" +
                "            \"quantifier\": {\n" +
                "              \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"properties\": [\n" +
                "                \"SINGLE\"\n" +
                "              ]\n" +
                "            },\n" +
                "            \"condition\": null,\n" +
                "            \"graph\": {\n" +
                "              \"nodes\": [\n" +
                "                {\n" +
                "                  \"name\": \"order-confirmation\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"SINGLE\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'order_confirmed'\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": null,\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"payment-processing\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"SINGLE\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'process_payment'\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": null,\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"shipping-selection\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"SINGLE\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'select_shipping'\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": null,\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"start-checkout\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"STRICT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"SINGLE\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'start_checkout'\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": null,\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                }\n" +
                "              ],\n" +
                "              \"edges\": [\n" +
                "                {\n" +
                "                  \"source\": \"payment-processing\",\n" +
                "                  \"target\": \"order-confirmation\",\n" +
                "                  \"type\": \"SKIP_TILL_NEXT\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"source\": \"shipping-selection\",\n" +
                "                  \"target\": \"payment-processing\",\n" +
                "                  \"type\": \"SKIP_TILL_NEXT\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"source\": \"start-checkout\",\n" +
                "                  \"target\": \"shipping-selection\",\n" +
                "                  \"type\": \"SKIP_TILL_NEXT\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            \"times\": null,\n" +
                "            \"untilCondition\": null,\n" +
                "            \"window\": {\n" +
                "              \"type\": \"FIRST_AND_LAST\",\n" +
                "              \"time\": {\n" +
                "                \"unit\": \"MINUTES\",\n" +
                "                \"size\": 15\n" +
                "              }\n" +
                "            },\n" +
                "            \"afterMatchSkipStrategy\": {\n" +
                "              \"type\": \"NO_SKIP\",\n" +
                "              \"patternName\": null\n" +
                "            },\n" +
                "            \"type\": \"COMPOSITE\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"cartGroup\",\n" +
                "            \"quantifier\": {\n" +
                "              \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"properties\": [\n" +
                "                \"SINGLE\"\n" +
                "              ]\n" +
                "            },\n" +
                "            \"condition\": null,\n" +
                "            \"graph\": {\n" +
                "              \"nodes\": [\n" +
                "                {\n" +
                "                  \"name\": \"cart-review\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"SINGLE\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'view_cart'\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": null,\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"modify-cart\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"SINGLE\",\n" +
                "                      \"OPTIONAL\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'cart_update' && (event.properties.action == 'quantity_change' || event.properties.action == 'remove_item')\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": null,\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"add-items\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"STRICT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"SINGLE\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'add_to_cart'\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": null,\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                }\n" +
                "              ],\n" +
                "              \"edges\": [\n" +
                "                {\n" +
                "                  \"source\": \"modify-cart\",\n" +
                "                  \"target\": \"cart-review\",\n" +
                "                  \"type\": \"SKIP_TILL_NEXT\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"source\": \"add-items\",\n" +
                "                  \"target\": \"modify-cart\",\n" +
                "                  \"type\": \"SKIP_TILL_NEXT\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            \"times\": null,\n" +
                "            \"untilCondition\": null,\n" +
                "            \"window\": null,\n" +
                "            \"afterMatchSkipStrategy\": {\n" +
                "              \"type\": \"NO_SKIP\",\n" +
                "              \"patternName\": null\n" +
                "            },\n" +
                "            \"type\": \"COMPOSITE\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"name\": \"browsingGroup\",\n" +
                "            \"quantifier\": {\n" +
                "              \"consumingStrategy\": \"STRICT\",\n" +
                "              \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "              \"properties\": [\n" +
                "                \"SINGLE\"\n" +
                "              ]\n" +
                "            },\n" +
                "            \"condition\": null,\n" +
                "            \"graph\": {\n" +
                "              \"nodes\": [\n" +
                "                {\n" +
                "                  \"name\": \"compare-products\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"SINGLE\",\n" +
                "                      \"OPTIONAL\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'compare' && event.properties.productCount >= 2\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": null,\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"product-view\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"LOOPING\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'view_product'\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": {\n" +
                "                    \"from\": 3,\n" +
                "                    \"to\": 3,\n" +
                "                    \"windowTime\": null\n" +
                "                  },\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"product-search\",\n" +
                "                  \"quantifier\": {\n" +
                "                    \"consumingStrategy\": \"STRICT\",\n" +
                "                    \"innerConsumingStrategy\": \"SKIP_TILL_NEXT\",\n" +
                "                    \"properties\": [\n" +
                "                      \"SINGLE\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  \"condition\": {\n" +
                "                    \"expression\": \"event.eventType == 'search' && event.properties.category == 'electronics'\",\n" +
                "                    \"type\": \"AVIATOR\"\n" +
                "                  },\n" +
                "                  \"times\": null,\n" +
                "                  \"untilCondition\": null,\n" +
                "                  \"window\": null,\n" +
                "                  \"afterMatchSkipStrategy\": {\n" +
                "                    \"type\": \"NO_SKIP\",\n" +
                "                    \"patternName\": null\n" +
                "                  },\n" +
                "                  \"type\": \"ATOMIC\"\n" +
                "                }\n" +
                "              ],\n" +
                "              \"edges\": [\n" +
                "                {\n" +
                "                  \"source\": \"product-view\",\n" +
                "                  \"target\": \"compare-products\",\n" +
                "                  \"type\": \"SKIP_TILL_NEXT\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"source\": \"product-search\",\n" +
                "                  \"target\": \"product-view\",\n" +
                "                  \"type\": \"SKIP_TILL_NEXT\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            \"times\": null,\n" +
                "            \"untilCondition\": null,\n" +
                "            \"window\": null,\n" +
                "            \"afterMatchSkipStrategy\": {\n" +
                "              \"type\": \"NO_SKIP\",\n" +
                "              \"patternName\": null\n" +
                "            },\n" +
                "            \"type\": \"COMPOSITE\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"edges\": [\n" +
                "          {\n" +
                "            \"source\": \"cartGroup\",\n" +
                "            \"target\": \"checkoutGroup\",\n" +
                "            \"type\": \"SKIP_TILL_NEXT\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"source\": \"browsingGroup\",\n" +
                "            \"target\": \"cartGroup\",\n" +
                "            \"type\": \"SKIP_TILL_NEXT\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"times\": null,\n" +
                "      \"untilCondition\": null,\n" +
                "      \"window\": null,\n" +
                "      \"afterMatchSkipStrategy\": {\n" +
                "        \"type\": \"NO_SKIP\",\n" +
                "        \"patternName\": null\n" +
                "      },\n" +
                "      \"type\": \"COMPOSITE\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    {\n" +
                "      \"source\": \"shoppingFlowGroup\",\n" +
                "      \"target\": \"afterSaleGroup\",\n" +
                "      \"type\": \"SKIP_TILL_NEXT\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        Pattern pattern = CepJsonUtils.convertJSONStringToPattern(json);
        System.out.println(pattern);

//        GraphSpec spec = CepJsonUtils.convertJSONStringToGraphSpec(json);
//        System.out.println(spec);
    }
}
