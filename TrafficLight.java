public class TrafficLight {
    package com.julie.store;

    public enum TrafficLightColor {

        GREEN(7), YELLOW(3), RED(30)
        private int second;

        TrafficLightColor(int second) {
            this.second = second;
        }

        public int getSecond(TrafficLightColor color) {
            return this.second;
        }

        public int changeSecond()
        }

}
