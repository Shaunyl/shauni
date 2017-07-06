package com.fil.shauni.command.writer.spi.montbs;

import com.fil.shauni.command.writer.WriterConfiguration;

/**
 *
 * @author Filippo
 */
public class AutoMonTbsWriter extends DefaultMonTbsWriter {

    public AutoMonTbsWriter(WriterConfiguration configuration) {
        super(configuration);
    }

//    @Deprecated
//    public AutoMonTbsWriter(Writer writer, int wthreshold, int cthreshold) {
//        super(writer, wthreshold, cthreshold);
//    }
//
//    public AutoMonTbsWriter(Writer writer, String host, String instance, int wthreshold, int cthreshold
//            , boolean undo, char unit, List<String> exclude, boolean growing) {
//        super(writer, host, instance, wthreshold, cthreshold, undo, unit, exclude, growing);
//    }

    @Override
    protected void retrieveTbsInfo(String[] record) {
        double autoDatafiles = Double.parseDouble(record[8]);
        String message = (autoDatafiles == 0f) ? "[OK]" : "[WARN(" + (int) autoDatafiles + ")]";

        long size_b = (long) Double.parseDouble(record[3]);
        long max_b = (long) Double.parseDouble(record[4]);
        long used_b = (long) Double.parseDouble(record[5]);
//        long free_b = (long)Double.parseDouble(record[6]);

        String size = convertToUnit(size_b);
        String max = convertToUnit(max_b);
        String used = convertToUnit(used_b);
//        String free = convertToUnit(free_b);

        String buffer = String.format("  %-10s%-55s%-25s%11.2f%%",
                c.getInstance(),
                record[1] + "[" + used + "/" + size + "/" + max + "]",
                "AUTOEXTEND" + message,
                //                "[" + used + "/" + free + "]",
                Float.valueOf(record[9]));
        writeNext(new String[]{ buffer });
    }
    
    @Override
    protected float getPct(String[] record) {
        return Float.parseFloat(record[9]);
    }
}
