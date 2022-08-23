package com.example.paperexchange.price;

import java.util.List;

public record Candles(List<Double> c, List<Double> h, List<Double> l, List<Double> o, String s, List<Long> t,
                      List<Long> v) {
}
