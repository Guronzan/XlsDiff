package fr.viveris.xlsdiff;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XLSDiffTest {
    private final XLSDiff xlsDiff = new XLSDiff();

    @Test
    public void testXlsDiffKO() throws FileNotFoundException, IOException {
        final boolean isOK = this.xlsDiff
                .performDiff(
                        "C:/travail_cvs/Workspace_Kawtar/OutilModelisation/Data/Donnees_BPL_V2_I.xls",
                        "C:/travail_cvs/Workspace_Kawtar/OutilModelisation/Data/Donnees_BPL_V2_I_KO_Pour_Diff.xls");
        assertFalse(isOK);
    }

    @Test
    public void testXlsDiffOK() throws FileNotFoundException, IOException {
        final boolean isOK = this.xlsDiff
                .performDiff(
                        "C:/travail_cvs/Workspace_Kawtar/OutilModelisation/Data/Donnees_BPL_V2_I.xls",
                        "C:/travail_cvs/Workspace_Kawtar/OutilModelisation/Data/Donnees_BPL_V2_I.xls");
        assertTrue(isOK);
    }
}
