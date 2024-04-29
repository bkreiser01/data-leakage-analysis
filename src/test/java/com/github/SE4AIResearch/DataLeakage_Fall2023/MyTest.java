package com.github.SE4AIResearch.DataLeakage_Fall2023;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.LeakageDetector;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.MultiTestLeakageDetector;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.PreprocessingLeakageDetector;
import org.junit.Test;

import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.OverlapLeakageDetector;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class MyTest {
    @Test
    public void isMultiDetector() {
        // Testing the listener developed and checking if it correctly listens for file changes
        // Creating a mock test by instantiating the detector class
        LeakageDetector lc = new MultiTestLeakageDetector();
        lc.FindLeakageInstances();
        Assert.assertTrue(lc.isLeakageDetected());
        //test if leakage detected comes out be t
    }

    @Test
    //only run this test if the leakage is detected to verify that the correct line numbers are being printed out
    public void MultiTestVerify() {
        LeakageDetector lc = new MultiTestLeakageDetector();
        List<LeakageInstance> test_list = lc.FindLeakageInstances();
        Invocation arbitraryInvocation = new Invocation("$invo0");
        LeakageInstance li1 = new MultiTestLeakageInstance(14, arbitraryInvocation);
        LeakageInstance li2 = new MultiTestLeakageInstance(4, arbitraryInvocation);
        List<LeakageInstance> cmp_list = new ArrayList<LeakageInstance>();
        cmp_list.add(li1);
        cmp_list.add(li2);
        System.out.print(test_list.size());
        for (int i = 0; i < test_list.size(); i++) {
            boolean is_true = test_list.get(i).equals(cmp_list.get(i));
            Assert.assertTrue(is_true);
        }


    }

    @Test
    //detected if overlap was found
    public void isOverlapDetector() {
        // Testing the listener developed and checking if it correctly listens for file changes
        // Creating a mock test by instantiating the detector class
        LeakageDetector lc = new OverlapLeakageDetector();
        lc.FindLeakageInstances();
        Assert.assertFalse(lc.isLeakageDetected());
        //test if leakage detected comes out be t
    }


    @Test
    //detect if preprocessing was found
    public void isPreprocessingDetector() {
        // Testing the listener developed and checking if it correctly listens for file changes
        // Creating a mock test by instantiating the detector class
        PreprocessingLeakageDetector lc = new PreprocessingLeakageDetector();
        lc.FindLeakageInstances();
        Assert.assertFalse(lc.isLeakageDetected());
        //test if leakage detected comes out be t
    }

}
