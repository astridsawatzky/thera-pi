package core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintWriter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FeatureTest {

    @Test
    public void aNewCreatedFeatureIsNotEnabled() throws Exception {
        Feature feature = new Feature("new");
        assertFalse(feature.isEnabled());
    }

    @Test
    public void whenAFeatureIsEnabledAnyFeatureWithTheSameNameIsEnabled() throws Exception {
        Feature originalFeature = new Feature("new");
        originalFeature.enable();
        Feature secondaryFeature = new Feature("new");
        assertTrue(secondaryFeature.isEnabled());

    }

    @Test
    public void featuresCanBeEnabledAndDisabled() throws Exception {
        Feature feature = new Feature("new");
        assertFalse(feature.isEnabled());
        feature.enable();
        assertTrue(feature.isEnabled());
        feature.disable();
        assertFalse(feature.isEnabled());
    }

    @Test
    public void featuresAreCaseinsensitive() throws Exception {

        assertEquals(new Feature("kleinGeschrieben"), new Feature("kleinGESCHRIEBEN"));

    }
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();


    @Test
    public void featuresCanBeEnabledViaFile() throws Exception {
        String featurename = "mysuperfeatUre";
        File file = folder.newFile("initme");
        PrintWriter pw = new PrintWriter(file);
        pw.println(featurename );
        pw.flush();
        pw.close();
        Feature.init(file);
        assertTrue(new Feature(featurename).isEnabled());

    }

}
