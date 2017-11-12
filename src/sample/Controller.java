package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
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
        tab(lower, x -> func(x) - 9);

        XYChart.Series<Number, Number> upper = new XYChart.Series<>();
        upper.setName("E(f) + b");
        tab(upper, x -> func(x) + 9);

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
        return Math.random() * 10;
    }

    private void initPoints(XYChart.Series<Number, Number> points) {
        for(int x=L; x<=R; x++) {
            points.getData().add(new XYChart.Data<>(x, 0D));
        }
    }

    private void tabInt(XYChart.Series<Number, Number> points, Function<Integer, Double> f) {
        for(int x=L; x<=R; x++) {
            points.getData().get(x-L).setYValue(f.apply(x-L));
        }
    }

    private Double expectation(int point) {
        double sum = 0;
        for(int i=0; i<N; ++i) {
            double value = gen.get(i).getData().get(point).getYValue().doubleValue();
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

    public Double func(Double x) {
        return x*x;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
