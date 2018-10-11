package snakeris;

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

    public boolean isOpposite(Direction dir){
        return false;
    }
}
