package com.scene.mesh.foundation.impl.helper;

import java.text.DecimalFormat;

public class DateHelper {

    public static Double calculateDuration(long startTime, long endTime) {
        double deltaTime = (endTime - startTime) / 1000.0; // 毫秒转秒
        DecimalFormat decimalFormat = new DecimalFormat("0.###"); // 保留 3 位小数
        String formattedTime = decimalFormat.format(deltaTime); // 格式化为字符串
        return Double.parseDouble(formattedTime); // 转换为 Double
    }

}
