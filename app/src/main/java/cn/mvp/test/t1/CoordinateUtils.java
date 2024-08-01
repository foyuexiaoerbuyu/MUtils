package cn.mvp.test.t1;


public class CoordinateUtils {

    /**
     * 计算两点之间的距离
     *
     * @param x1 第一个点的X坐标
     * @param y1 第一个点的Y坐标
     * @param x2 第二个点的X坐标
     * @param y2 第二个点的Y坐标
     * @return 两点之间的距离
     */
    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * 计算从指定圆心出发，经过两点的连线延长指定长度后的新坐标点
     *
     * @param centerX 圆心的X坐标
     * @param centerY 圆心的Y坐标
     * @param x1      第一个点的X坐标
     * @param y1      第一个点的Y坐标
     * @param x2      第二个点的X坐标
     * @param y2      第二个点的Y坐标
     * @param extend  需要延长的长度
     * @return 延长后的新坐标点 (newX, newY)
     */
    public static double[] calculateExtendedCoordinate(double centerX, double centerY, double x1, double y1, double x2, double y2, double extend) {
        // 计算圆心到第一个点的距离
        double distance1 = calculateDistance(centerX, centerY, x1, y1);

        // 计算圆心到第二个点的距离
        double distance2 = calculateDistance(centerX, centerY, x2, y2);

        // 计算两点之间的距离
        double distanceBetweenPoints = calculateDistance(x1, y1, x2, y2);

        // 计算新距离
        double newDistance = distanceBetweenPoints + extend;

        // 计算比例
        double ratio = newDistance / distanceBetweenPoints;

        // 计算新坐标
        double newX = centerX + (x2 - centerX) * ratio;
        double newY = centerY + (y2 - centerY) * ratio;

        return new double[]{newX, newY};
    }

    /**
     * 计算两个坐标点之间的相对角度
     *
     * @param centerX 圆心的X坐标
     * @param centerY 圆心的Y坐标
     * @param pointX  要计算的点的X坐标
     * @param pointY  要计算的点的Y坐标
     * @return 相对角度（以度为单位）
     */
    public static double calculateAngle(double centerX, double centerY, double pointX, double pointY) {
        // 计算相对坐标
        double deltaX = pointX - centerX;
        double deltaY = pointY - centerY;
        // 使用atan2计算角度（结果为弧度）
        double angleInRadians = Math.atan2(deltaY, deltaX);
        // 将弧度转换为度
        double angleInDegrees = Math.toDegrees(angleInRadians);
        // 确保角度在0到360度之间
        if (angleInDegrees < 0) {
            angleInDegrees += 360;
        }
        return angleInDegrees;
    }


    /**
     * 判断某个点是否在以指定圆心和半径的圆内
     *
     * @param centerX 圆心的X坐标
     * @param centerY 圆心的Y坐标
     * @param radius  圆的半径
     * @param pointX  要判断的点的X坐标
     * @param pointY  要判断的点的Y坐标
     * @return 如果点在圆内或在圆上，返回true；否则返回false
     */
    public static boolean isPointInCircle(double centerX, double centerY, double radius, double pointX, double pointY) {
        // 计算点到圆心的距离
        double distance = Math.sqrt(Math.pow(pointX - centerX, 2) + Math.pow(pointY - centerY, 2));
        // 判断距离是否小于等于半径
        return distance <= radius;
    }

    public static void main(String[] args) {
        // 测试示例
        double centerX = 0.0;
        double centerY = 0.0;

        double x1 = 1.0;
        double y1 = 1.0;

        double x2 = 4.0;
        double y2 = 5.0;

        double extend = 2.0;

        double[] newCoordinates = calculateExtendedCoordinate(centerX, centerY, x1, y1, x2, y2, extend);

        System.out.println("New coordinates after extending: (" + newCoordinates[0] + ", " + newCoordinates[1] + ")");
    }
}
