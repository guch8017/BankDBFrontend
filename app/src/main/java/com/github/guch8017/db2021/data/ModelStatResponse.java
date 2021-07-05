package com.github.guch8017.db2021.data;

import java.util.List;

public class ModelStatResponse extends ModelResponseBase{
    public static class Stat1{
        public String date;
        public int count;
        public String branch;
        public double fund;
    }
    public List<Stat1> data;
}
