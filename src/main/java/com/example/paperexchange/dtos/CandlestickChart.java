package com.example.paperexchange.dtos;

import java.util.List;

public record CandlestickChart(List<Double> c, List<Double> h, List<Double> l, List<Double> o, String s, List<Long> t,
                               List<Long> v) {
}
