package snakeris;

/**
 * аправление движения
 */
public enum Direction {
    LEFT{
        @Override
        public boolean isOpposite(Direction dir) {
            return dir==RIGHT;
        }
    },
    RIGHT{
        @Override
        public boolean isOpposite(Direction dir) {
            return dir.equals(LEFT);
        }
    },
    TOP{
        @Override
        public boolean isOpposite(Direction dir) {
            return dir.equals(BOTTOM);
        }
    },
    BOTTOM{
        @Override
        public boolean isOpposite(Direction dir) {
            return dir.equals(TOP);
        }
    };

    /**
     * Проверяет, является ли переданное направление противоположным данному
     * @param dir
     * @return {@code true} если направления противоположны
     */
    public boolean isOpposite(Direction dir){
        return false;
    }
}
