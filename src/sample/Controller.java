package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private ObservableList<XYChart.Series<Number, Number>> chartData;

    private ObservableList<XYChart.Series<Number, Number>> gen;

    final int L = -4;
    final int R = 5;
    final int N = 10;

    @FXML
    private LineChart chart;

    public void btnClicked () {
        for(int i=0; i<N; ++i) {
            tabInt(gen.get(i), this::rand);
        }
        tabInt(chartData.get(3), this::expectation);
    }

    public ObservableList<XYChart.Series<Number, Number>> getSeries()
    {
        XYChart.Series<Number, Number> lower = new XYChart.Series<>();

        lower.setName("E(f) - b");
        tab(lower, x -> func(x) - 3 * b(x));

        XYChart.Series<Number, Number> upper = new XYChart.Series<>();
        upper.setName("E(f) + b");
        tab(upper, x -> func(x) + 3 * b(x));

        XYChart.Series<Number, Number> expectation = new XYChart.Series<>();
        expectation.setName("E(f)");
        tab(expectation, this::func);

        gen = FXCollections.observableArrayList();
        for(int i=0; i<N; ++i){
            XYChart.Series<Number, Number> gen_i = new XYChart.Series<>();
            gen_i.setName("f" + i);
            initPoints(gen_i);
            tabInt(gen_i, this::rand);
            gen.add(gen_i);
        }

        XYChart.Series<Number, Number> expectation2 = new XYChart.Series<>();
        expectation2.setName("E_(f)");
        initPoints(expectation2);
        tabInt(expectation2, this::expectation);

        ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();
        data.add(expectation);
        data.add(lower);
        data.add(upper);
        data.add(expectation2);
        data.addAll(gen);

        return data;
    }

    private Double rand(int x) {
        int pos = ((int) (Math.random() * 1e+9)) % V.size();
        double eta = V.get(pos);
        return ksi(eta, x);
    }

    private void initPoints(XYChart.Series<Number, Number> points) {
        for(int x=L; x<=R; x++) {
            points.getData().add(new XYChart.Data<>(x, 0D));
        }
    }

    private void tabInt(XYChart.Series<Number, Number> points, Function<Integer, Double> f) {
        for(int x=L; x<=R; x++) {
            points.getData().get(x-L).setYValue(f.apply(x));
        }
    }

    private Double expectation(int point) {
        double sum = 0;
        for(int i=0; i<N; ++i) {
            double value = gen.get(i).getData().get(point-L).getYValue().doubleValue();
            sum += value;
        }
        return sum / N;
    }

    void tab(XYChart.Series points, Function<Double, Double> f) {
        double h = (R-L) / 1000.0;
        for(double x=L; x<=R; x+=h) {
            points.getData().add(new XYChart.Data<>(x, f.apply(x)));
        }
    }

    double ksi(double eta, double t) {
        return eta*(t*t - 2*t + 3) - Math.sin(t);
    }

    public Double b(Double t) {
        return 3 * (t*t - 2*t + 3);
    }

    public Double func(Double x) {
        return ksi(2, x);
    }

    private List<Double> V = new ArrayList<>();

    double gauss(double x, double a, double d) {
        return Math.exp((x-a)*(a-x)/(2*d*d)) / Math.sqrt(2 * Math.PI) / d;
    }

    void G(double a, double d, double lr, int iter) {
        double l = a - lr;
        double r = a + lr;
        double step = (r-l) / iter;
        while (l < r) {
            System.out.println(l);
            double v = gauss(l, a, d);
            int x = (int) (v*1000);
            for(int i=0; i<x; ++i){
                V.add(l);
            }
            l+=step;
        }
        System.out.println(V.size());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        G(2,3, 10, 1000);
        chartData = getSeries();
        chart.setData(chartData);
        addStyles();
    }

    private void addStyles() {
        Consumer<Integer> addStyle1 = (x) -> {
            chartData.get(x).getNode().setStyle("" +
                    "-fx-stroke-width: 1px; " +
                    "-fx-stroke-dash-array: 5 5;");
        };
        addStyle1.accept(1);
        addStyle1.accept(2);
        chartData.get(0).getNode().setStyle(("" +
                "-fx-stroke-width: 2px;" +
                "-fx-opacity: 0.5;" +
                "-fx-stroke: red"));

        Consumer<Integer> addStyleGen = i -> {
            chartData.get(i+4).getNode().setStyle("" +
                    "-fx-stroke-width: 1px;" +
                    "-fx-opacity: 0.3;");
        };

        IntStream.range(0, N).forEach(i -> addStyleGen.accept(i));
    }

}
