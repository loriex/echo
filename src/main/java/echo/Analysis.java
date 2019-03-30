package echo;

import java.util.Collections;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class Analysis {
    static void Generate(List<Long> times) {
        Collections.sort(times);

        long mi = times.get(0);
        long ma = times.get(times.size()-1) + 1;
        //divide into ${div} parts.
        int div = 20;
        
        int[] count = new int[div];
        int[] arr=new int[6];
        for (Long ti : times) {
            int index = (int) ((ti - mi) * div / (ma - mi));
            // System.out.println(index);
            count[index] = count[index] + 1;
        }
        for (int i = 0; i < div; ++i) {
            long l = mi + (ma-mi)/div * i;
            long r = mi + (ma-mi)/div * (i+1);
            System.out.println("response time(us) ["+l+","+r+") count: "+count[i]);
        }
    }
}