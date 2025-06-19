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
        String json = "{\"nodes\":[{\"name\":\"afterSaleGroup\",\"quantifier\":{\"consumingStrategy\":\"SKIP_TILL_NEXT\",\"innerConsumingStrategy\":\"SKIP_TILL_NEXT\",\"properties\":[\"TIMES\"]},\"condition\":{\"className\":null,\"type\":\"CLASS\"},\"times\":{\"from\":1,\"to\":5,\"windowTime\":null},\"untilCondition\":null,\"window\":{\"type\":\"FIRST_AND_LAST\",\"time\":{\"unit\":\"DAYS\",\"size\":7}},\"afterMatchSkipStrategy\":{\"type\":\"NO_SKIP\",\"patternName\":null},\"type\":\"ATOMIC\"},{\"name\":\"shoppingFlowGroup\",\"quantifier\":{\"consumingStrategy\":\"STRICT\",\"innerConsumingStrategy\":\"SKIP_TILL_NEXT\",\"properties\":[\"SINGLE\"]},\"condition\":{\"className\":null,\"type\":\"CLASS\"},\"times\":null,\"untilCondition\":null,\"window\":null,\"afterMatchSkipStrategy\":{\"type\":\"NO_SKIP\",\"patternName\":null},\"type\":\"ATOMIC\"}],\"edges\":[{\"source\":\"shoppingFlowGroup\",\"target\":\"afterSaleGroup\",\"type\":\"SKIP_TILL_NEXT\"}]}\n";
        Pattern pattern = CepJsonUtils.convertJSONStringToPattern(json);
        System.out.println(pattern);

//        GraphSpec spec = CepJsonUtils.convertJSONStringToGraphSpec(json);
//        System.out.println(spec);
    }
}
