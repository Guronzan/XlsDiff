package fr.viveris.xlsdiff;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class XLSDiffTest {
    private final XLSDiff xlsDiff = new XLSDiff();

    @Test
    public void testXlsDiff() throws FileNotFoundException, IOException {
        this.xlsDiff
                .performDiff(
                        "C:/travail_cvs/Workspace_Kawtar/OutilModelisation/Data/Donnees_BPL_V2_I.xls",
                        "C:/travail_cvs/Workspace_Kawtar/OutilModelisation/Data/Donnees_BPL_V2_I_KO_Pour_Diff.xls");
    }
}
