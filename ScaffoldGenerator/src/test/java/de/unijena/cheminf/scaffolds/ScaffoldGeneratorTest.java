/*
 * Copyright (c) 2021 Julian Zander, Jonas Schaub, Achim Zielesny, Christoph Steinbeck
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, version 2.1.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.unijena.cheminf.scaffolds;

import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.fragment.MurckoFragmenter;
import org.openscience.cdk.graph.CycleFinder;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.io.FormatFactory;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV3000Reader;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;

/**
 * JUnit test class for the ScaffoldGenerator
 *
 * @author Julian Zander, Jonas Schaub (zanderjulian@gmx.de, jonas-schaub@uni-jena.de)
 * @version 1.0.0.0
 */
public class ScaffoldGeneratorTest extends ScaffoldGenerator {

    //<editor-fold desc="Tests">
    //<editor-fold desc="Fundamental method tests">
    /**
     * Test of ScaffoldGenerator.getScaffold() with V2000 and V3000 mol files.
     * Loads the 22 Test(Test1.mol-Test23.mol) molfiles from the Resources folder and creates the SchuffenhauerScaffolds with getScaffold().
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * The subfolder has the name of the input file.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScaffoldTest() throws Exception {
        for (int tmpCount = 1; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            /*Generate picture of the original*/
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMolecule);
            CDKHydrogenAdder.getInstance(tmpMolecule.getBuilder()).addImplicitHydrogens(tmpMolecule);
            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
            BufferedImage tmpImgOriginal = tmpGenerator.depict(tmpMolecule).toImg();
            /*Save the original picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
            File tmpOutputOriginal = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/Original.png");
            ImageIO.write(tmpImgOriginal, "png" ,tmpOutputOriginal);
            //Generate SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            //Generate picture of the SchuffenhauerScaffold
            BufferedImage tmpImgSchuff = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
            File tmpOutputSchuff = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/SchuffenhauerScaffold.png");
            ImageIO.write(tmpImgSchuff, "png" ,tmpOutputSchuff);
        }
    }

    /**
     * Test of ScaffoldGenerator.getScaffold() with V2000 and V3000 mol files.
     * Loads the 22 Test(Test1.mol-Test23.mol) molfiles from the Resources folder and creates the SchuffenhauerScaffolds with getScaffold().
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * The subfolder has the name of the input file.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScaffoldWithoutHTest() throws Exception {
        for (int tmpCount = 1; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            /*Generate picture of the original*/
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMolecule);
            CDKHydrogenAdder.getInstance(tmpMolecule.getBuilder()).addImplicitHydrogens(tmpMolecule);
            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
            BufferedImage tmpImgOriginal = tmpGenerator.depict(tmpMolecule).toImg();
            /*Save the original picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
            File tmpOutputOriginal = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/Original.png");
            ImageIO.write(tmpImgOriginal, "png" ,tmpOutputOriginal);
            //Generate SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
            //Generate picture of the SchuffenhauerScaffold
            BufferedImage tmpImgSchuff = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
            File tmpOutputSchuff = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/SchuffenhauerScaffoldWithoutH.png");
            ImageIO.write(tmpImgSchuff, "png" ,tmpOutputSchuff);
        }
    }
    /**
     * Test of ScaffoldGenerator.getScaffold with SMILES.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getSchuffenhauerNonCTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("[S+]#S1CCCCC1"); //Triple bond
        //IAtomContainer tmpMolecule = tmpParser.parseSmiles("P=[P+]1CCCCC1"); // P=P Test
        tmpMolecule = tmpParser.parseSmiles("S=P12N=P3(OC=4C=CC(C=NN(C)P(=S)(C=5C=CC=CC5)N(N=CC=6C=CC(OP(=NP(=S)(OC=7C=CC(C=NN(C)P(=S)(C=8C=CC=CC8)N(N=CC=9C=CC(O1)=CC9)C)=CC7)OC=%10C=CC(C=NN(C)P(=S)(C=%11C=CC=CC%11)N(N=CC=%12C=CC(O2)=CC%12)C)=CC%10)(OC=%13C=CC(C=NN(C)P(=S)(C=%14C=CC=CC%14)N(N=CC=%15C=CC(O3)=CC%15)C)=CC%13)C=%16C=CC=CC%16)=CC6)C)=CC4)C=%17C=CC=CC%17"); // Test
        //IAtomContainer tmpMolecule = tmpParser.parseSmiles("S=S1CCCCC1"); //S=S Test
        /*Generate picture of the Original*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        tmpMolecule = AtomContainerManipulator.removeHydrogens(tmpMolecule);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCDoubleBond").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCDoubleBond/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold*/
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuff = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSchuff = tmpGenerator.depict(tmpSchuff).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCDoubleBond").mkdirs();
        File tmpOutputSchuff = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCDoubleBond/Schuffenhauer.png");
        ImageIO.write(tmpImgSchuff, "png" ,tmpOutputSchuff);
        //Generate rings
        List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRingsInternal(tmpScaffoldGenerator.getScaffold(tmpMolecule, true), true);
        /*Generate pictures of the rings*/
        int tmpCounter = 1;
        for (IAtomContainer tmpRing : tmpRings) {
            BufferedImage tmpImgRing = tmpGenerator.depict(tmpRing).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCDoubleBond").mkdirs();
            File tmpOutputRing = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCDoubleBond/Ring" + tmpCounter + ".png");
            ImageIO.write(tmpImgRing, "png", tmpOutputRing);
            tmpCounter++;
        }
    }

    /**
     * Test of ScaffoldGenerator.getScaffold with SMILES.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void removeRingNonCTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("c2cc[p+]1CCCCc1c2"); //Triple bond
        tmpMolecule = tmpParser.parseSmiles("c1cc2CCCc3cccc(c1)c23");//Test
        //tmpMolecule = tmpParser.parseSmiles("c1ccc3c(c1)c2ccccc2c4ccccc34");//Test
        //tmpMolecule = tmpParser.parseSmiles("c1ccc2c(c1)c7cccc6CC3CCc4cccc5cc2c(c3c45)c67");//Test
        tmpMolecule = tmpParser.parseSmiles("CCN(C1=CC=CC(=C1)C2=CC=NC3=C(C=NN23)C#N)C(=O)C");//Test
        //tmpMolecule = tmpParser.parseSmiles("c7ccc(c5c(c1ccccc1)c(c2ccccc2)c(c3ccccc3)c(c4ccccc4)c5c6ccccc6)cc7");//Test
        //tmpMolecule = tmpParser.parseSmiles("c1c3CCC4CCC5CCC6CCc7cc2CCc1c2c8c3C4=C5C6c78");//Test
        /*Generate picture of the Original*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCRemove").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCRemove/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold*/
        ScaffoldGenerator tmpScaffoldGenerator = null;
        try {
            tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        IAtomContainer tmpSchuff = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSchuff = tmpGenerator.depict(tmpSchuff).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCRemove").mkdirs();
        File tmpOutputSchuff = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCRemove/Schuffenhauer.png");
        ImageIO.write(tmpImgSchuff, "png" ,tmpOutputSchuff);
        //Generate rings
        List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRingsInternal(tmpScaffoldGenerator.getScaffold(tmpMolecule, true),true);
        /*Generate pictures of the rings*/
        int tmpCounter = 1;
        for (IAtomContainer tmpRing : tmpRings) {
            IAtomContainer tmpRingRemoved =tmpScaffoldGenerator.removeRing(tmpSchuff, true, tmpRing);
            BufferedImage tmpImgRingRemoved = tmpGenerator.depict(tmpRingRemoved).toImg();
            BufferedImage tmpImgRing = tmpGenerator.depict(tmpRing).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCRemove").mkdirs();
            File tmpOutputRingRemoved = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCRemove/Ring" + tmpCounter + "Removed.png");
            ImageIO.write(tmpImgRingRemoved, "png", tmpOutputRingRemoved);
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCRemove").mkdirs();
            File tmpOutputRing = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Miscellaneous/NonCRemove/Ring" + tmpCounter + ".png");
            ImageIO.write(tmpImgRing, "png", tmpOutputRing);
            tmpCounter++;
        }
    }
    /**
     * Test of Cycles.mcb() with V2000 and V3000 mol files.
     * Loads the 12 Test(Test1.mol-Test12.mol) molfiles from the Resources folder and creates the rings of the SchuffenhauerScaffold with getRings().
     * All generated Rings are saved as images in a subfolder of the scaffoldTestOutput folder.
     * The subfolder has the name of the input file.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getRingsTest() throws Exception {
        for (int tmpCount = 1; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate the SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            //tmpMolecule = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            //Generate rings
            List<IAtomContainer> tmpRings =tmpScaffoldGenerator.getRings(tmpMolecule,true, true);
            /*Generate pictures of the rings*/
            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
            int tmpCounter = 1;
            for (IAtomContainer tmpRing : tmpRings) {
                BufferedImage tmpImgRing = tmpGenerator.depict(tmpRing).toImg();
                /*Save the picture*/
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
                File tmpOutputRing = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/GeneratedRing" + tmpCounter + ".png");
                ImageIO.write(tmpImgRing, "png", tmpOutputRing);
                tmpCounter++;
            }
        }
    }

    /**
     * Test of removeRing() with V2000 and V3000 mol files.
     * Loads the 12 Test(Test1.mol-Test12.mol) molfiles from the Resources folder and creates for each generated ring, the corresponding total molecule with removed ring.
     * All generated molecules are saved as images in a subfolder of the scaffoldTestOutput folder.
     * The subfolder has the name of the input file.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void removeRingTest() throws Exception {
        for (int tmpCount = 2; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount ;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            //Generate Rings
            List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRings(tmpSchuffenhauerScaffold, true, true);
            int tmpCounter = 1;
            for (IAtomContainer tmpRing : tmpRings) {
                /*Generate SchuffenhauerScaffold with removed ring*/
                //tmpSchuffenhauerScaffold =tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, tmpRing);
                IAtomContainer tmpRemovedSchuff = tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, true, tmpRing);
                /*Generate picture of the SchuffenhauerScaffold with removed ring*/
                DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                BufferedImage tmpImgRemove = tmpGenerator.depict(tmpRemovedSchuff).toImg();
                /*Save the picture*/
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
                File tmpOutputRemove = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/RingRemove" + tmpCounter + ".png");
                ImageIO.write(tmpImgRemove, "png", tmpOutputRemove);
                tmpCounter++;
            }
        }
    }

    /**
     * Test of removeRing() with V2000 and V3000 mol files.
     * Loads the 12 Test(Test1.mol-Test12.mol) molfiles from the Resources folder and creates for each generated ring, the corresponding total molecule with removed ring.
     * All generated molecules are saved as images in a subfolder of the scaffoldTestOutput folder.
     * The subfolder has the name of the input file.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void removeRingWithoutHTest() throws Exception {
        for (int tmpCount = 2; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount ;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            //Generate Rings
            List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRings(tmpSchuffenhauerScaffold, true,true);
            int tmpCounter = 1;
            for (IAtomContainer tmpRing : tmpRings) {
                /*Generate SchuffenhauerScaffold with removed ring*/
                //tmpSchuffenhauerScaffold =tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, tmpRing);
                IAtomContainer tmpRemovedSchuff = tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, false, tmpRing);
                /*Generate picture of the SchuffenhauerScaffold with removed ring*/
                DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                BufferedImage tmpImgRemove = tmpGenerator.depict(tmpRemovedSchuff).toImg();
                /*Save the picture*/
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName ).mkdirs();
                File tmpOutputRemove = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/RingRemove" + tmpCounter + "WithoutH.png");
                ImageIO.write(tmpImgRemove, "png", tmpOutputRemove);
                tmpCounter++;
            }
        }
    }

    /**
     * Example of the errors caused by AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms().
     * All molecules containing B throw a NullPointerException in line 347 of the CDKAtomMatcher.
     * All molecules containing P with 4 bonds throw a NullPointerException in line 1373 of the CDKAtomMatcher.
     * In both cases Atom.getFormalCharge() = null there and triggers this error.
     * In this method, both an example molecule with B and with P are given. Both trigger an error. With tmpBypassError the FormalCharges can be set to 0 and the errors can be bypassed.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void percieveAtomTypesErrorTest() throws Exception {
        boolean tmpBypassError = false; //Pass the error or not
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = null;
        //P Examples:
        tmpMolecule = tmpParser.parseSmiles("C1C2CC3CC1CC(C2)C3(CC(=O)C(=P(C4=CC=CC=C4)(C5=CC=CC=C5)C6=CC=CC=C6)C#N)C7=CC=C(C=C7)F"); //PubChem CID: 89041793
        //tmpMolecule = tmpParser.parseSmiles("C1=CC=C(C=C1)C(=O)OCCCC[P+](C2=CC=CC=C2)(C3=CC=CC=C3)C4=CC=CC=C4"); //PubChem CID: 2755300
        //tmpMolecule = tmpParser.parseSmiles("C1CCC(=C(C(=P(C2=CC=CC=C2)(C3=CC=CC=C3)C4=CC=CC=C4)C(=O)C(C(F)(F)F)(F)F)C(C(F)(F)F)(F)F)C1"); //PubChem CID: 2752540
        //B Examples:
        //tmpMolecule = tmpParser.parseSmiles("[B-](C1=CC=CC=C1)(C2=CC=CC=C2)(C3=CC=CC=C3)C4=CC=CC=C4"); //PubChem CID: 8934
        //tmpMolecule = tmpParser.parseSmiles("[B-]123OC4C(=O)OC(CCC=CC=CCCC(CC(=O)C(C5CCC(C(O1)(O5)C(O2)C(=O)OC(CCC=CC=CCCC(CC(=O)C(C6CCC(C4(O3)O6)(C)O)C)O)C)(C)O)C)O)C"); //PubChem CID: 637168
        //tmpMolecule = tmpParser.parseSmiles("[B-]123OC4C(=O)OC5CC(C=CCC(C(C6CCC(C(O1)(O6)C(O2)C(=O)OC7CC(C=CCC(C(C8CCC(C4(O3)O8)C)(C)C)O)OC7C)C)(C)C)O)OC5C"); //PubChem CID: 43587
        //AtomContainerManipulator.clearAtomConfigurations(tmpMolecule);
        for(IAtom tmpAtom : tmpMolecule.atoms()) {
            tmpAtom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
        }
        if(tmpBypassError) {
            /*Avoid the error by setting the FormalCharge to 0*/
            for(IAtom tmpAtom : tmpMolecule.atoms()) {
                if(tmpAtom.getSymbol() == "B" || (tmpAtom.getSymbol() == "P" && tmpMolecule.getConnectedBondsList(tmpAtom).size() == 4)) {
                    tmpAtom.setFormalCharge(0);
                }
            }
        }
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMolecule); //Line that causes the error
    }

    /**
     * Test of isRingTerminal() with V2000 and V3000 mol files.
     * Loads the 12 Test(Test1.mol-Test12.mol) molfiles from the Resources folder and creates for each generated terminal ring, the corresponding total molecule with removed ring.
     * All generated molecules are saved as images in a subfolder of the scaffoldTestOutput folder.
     * The subfolder has the name of the input file.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void isRingTerminalTest() throws Exception {
        for (int tmpCount = 2; tmpCount < 21; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            //Generate Rings
            List<IAtomContainer> tmpRings =tmpScaffoldGenerator.getRingsInternal(tmpSchuffenhauerScaffold, true);
            int tmpCounter = 1;
            for (IAtomContainer tmpRing : tmpRings) {
                //Check that rings are terminal
                if(tmpScaffoldGenerator.isRingTerminal(tmpSchuffenhauerScaffold, tmpRing)) {
                    //Generate SchuffenhauerScaffold with removed ring
                    IAtomContainer tmpRemovedSchuff =tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, true, tmpRing);
                    /*Generate picture of the SchuffenhauerScaffold with removed ring*/
                    DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                    BufferedImage tmpImgRemove = tmpGenerator.depict(tmpRemovedSchuff).toImg();
                    /*Save the picture*/
                    new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
                    File tmpOutputRemove = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/TerminalRingRemove" + tmpCounter + ".png");
                    ImageIO.write(tmpImgRemove, "png", tmpOutputRemove);
                }
                tmpCounter++;
            }
        }
    }

    /**
     * Stores the side chains of the Schuffenhauer scaffold of all test molecules as images
     * The images are saved in the folder with the name of the test molecule. By removing the annotation, side chains from other scaffolds can also be output.
     * @throws Exception If anything goes wrong
     */
    @Test
    public void getSideChainTest() throws Exception {
        for (int tmpCount = 1; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate the SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            //tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
            //tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
            //Generate SideChains
            List<IAtomContainer> tmpSideChains = tmpScaffoldGenerator.getSideChains(tmpMolecule, true);
            /*Generate pictures of the SideChains*/
            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
            int tmpCounter = 1;
            for (IAtomContainer tmpSideChain : tmpSideChains) {
                BufferedImage tmpImgtmpSideChain = tmpGenerator.depict(tmpSideChain).toImg();
                /*Save the picture*/
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
                File tmpOutputtmpSideChain = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/SideChain" + tmpCounter + ".png");
                ImageIO.write(tmpImgtmpSideChain, "png", tmpOutputtmpSideChain);
                tmpCounter++;
            }
        }
    }

    /**
     * Stores the side chains of the Schuffenhauer scaffold of all test molecules as images
     * The images are saved in the folder with the name of the test molecule. By removing the annotation, side chains from other scaffolds can also be output.
     * @throws Exception If anything goes wrong
     */
    @Test
    public void getSideChainWithoutHTest() throws Exception {
        for (int tmpCount = 1; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate the SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            //tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
            //tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
            //Generate SideChains
            List<IAtomContainer> tmpSideChains = tmpScaffoldGenerator.getSideChains(tmpMolecule, false);
            /*Generate pictures of the SideChains*/
            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
            int tmpCounter = 1;
            for (IAtomContainer tmpSideChain : tmpSideChains) {
                BufferedImage tmpImgtmpSideChain = tmpGenerator.depict(tmpSideChain).toImg();
                /*Save the picture*/
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
                File tmpOutputtmpSideChain = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/SideChain" + tmpCounter + "WithoutH.png");
                ImageIO.write(tmpImgtmpSideChain, "png", tmpOutputtmpSideChain);
                tmpCounter++;
            }
        }
    }

    /**
     * Creates the SideChains for the Schuffenhauer Scaffold, the Murcko Framework and the Beccari Basic Framework. Since the SideChains of the Beccari Basic Framework correspond to those of the Elemental Wire Frame and the Beccari Wire Framework, all possible side chains are covered.
     * The images of the side chains are saved.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getSideChainScaffoldModeTest() throws Exception {
        String tmpFileName = "Test11";
        //Load molecule from molfile
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
        //Generate the SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        //Generate SideChains
        List<IAtomContainer> tmpSideChainsSchuff = tmpScaffoldGenerator.getSideChains(tmpMolecule, true);
        /*Generate pictures of the rings*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        int tmpCounter = 1;
        for (IAtomContainer tmpSideChain : tmpSideChainsSchuff) {
            BufferedImage tmpImgtmpSideChain = tmpGenerator.depict(tmpSideChain).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Schuffenhauer").mkdirs();
            File tmpOutputtmpSideChain = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Schuffenhauer/" + tmpCounter + ".png");
            ImageIO.write(tmpImgtmpSideChain, "png", tmpOutputtmpSideChain);
            tmpCounter++;
        }
        /*Murcko framework SideChains*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
        //Generate SideChains
        List<IAtomContainer> tmpSideChainsMurcko = tmpScaffoldGenerator.getSideChains(tmpMolecule, true);
        /*Generate pictures of the rings*/
        tmpCounter = 1;
        for (IAtomContainer tmpSideChain : tmpSideChainsMurcko) {
            BufferedImage tmpImgtmpSideChain = tmpGenerator.depict(tmpSideChain).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Murcko").mkdirs();
            File tmpOutputtmpSideChain = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Murcko/" + tmpCounter + ".png");
            ImageIO.write(tmpImgtmpSideChain, "png", tmpOutputtmpSideChain);
            tmpCounter++;
        }
        /*Beccari Basic Framework SideChains
        * These are identical with Elemental Wire Frame and Beccari Wire Framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.BECCARI_BASIC_FRAMEWORK);
        //Generate SideChains
        List<IAtomContainer> tmpSideChainsBeccari = tmpScaffoldGenerator.getSideChains(tmpMolecule, true);
        /*Generate pictures of the rings*/
        tmpCounter = 1;
        for (IAtomContainer tmpSideChain : tmpSideChainsBeccari) {
            BufferedImage tmpImgtmpSideChain = tmpGenerator.depict(tmpSideChain).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Beccari").mkdirs();
            File tmpOutputtmpSideChain = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Beccari/" + tmpCounter + ".png");
            ImageIO.write(tmpImgtmpSideChain, "png", tmpOutputtmpSideChain);
            tmpCounter++;
        }
        
    }

    /**
     * Creates the SideChains for the Schuffenhauer Scaffold, the Murcko Framework and the Beccari Basic Framework. Since the SideChains of the Beccari Basic Framework correspond to those of the Elemental Wire Frame and the Beccari Wire Framework, all possible side chains are covered.
     * The images of the side chains are saved.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getSideChainScaffoldModeWithoutHTest() throws Exception {
        String tmpFileName = "Test11";
        //Load molecule from molfile
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
        //Generate the SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        //Generate SideChains
        List<IAtomContainer> tmpSideChainsSchuff = tmpScaffoldGenerator.getSideChains(tmpMolecule, false);
        /*Generate pictures of the rings*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        int tmpCounter = 1;
        for (IAtomContainer tmpSideChain : tmpSideChainsSchuff) {
            BufferedImage tmpImgtmpSideChain = tmpGenerator.depict(tmpSideChain).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Schuffenhauer").mkdirs();
            File tmpOutputtmpSideChain = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Schuffenhauer/" + tmpCounter + "WithoutH.png");
            ImageIO.write(tmpImgtmpSideChain, "png", tmpOutputtmpSideChain);
            tmpCounter++;
        }
        /*Murcko framework SideChains*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
        //Generate SideChains
        List<IAtomContainer> tmpSideChainsMurcko = tmpScaffoldGenerator.getSideChains(tmpMolecule, false);
        /*Generate pictures of the rings*/
        tmpCounter = 1;
        for (IAtomContainer tmpSideChain : tmpSideChainsMurcko) {
            BufferedImage tmpImgtmpSideChain = tmpGenerator.depict(tmpSideChain).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Murcko").mkdirs();
            File tmpOutputtmpSideChain = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Murcko/" + tmpCounter + "WithoutH.png");
            ImageIO.write(tmpImgtmpSideChain, "png", tmpOutputtmpSideChain);
            tmpCounter++;
        }
        /*Beccari Basic Framework SideChains
         * These are identical with Elemental Wire Frame and Beccari Wire Framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.BECCARI_BASIC_FRAMEWORK);
        //Generate SideChains
        List<IAtomContainer> tmpSideChainsBeccari = tmpScaffoldGenerator.getSideChains(tmpMolecule, false);
        /*Generate pictures of the rings*/
        tmpCounter = 1;
        for (IAtomContainer tmpSideChain : tmpSideChainsBeccari) {
            BufferedImage tmpImgtmpSideChain = tmpGenerator.depict(tmpSideChain).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Beccari").mkdirs();
            File tmpOutputtmpSideChain = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SideChain/Beccari/" + tmpCounter + "WithoutH.png");
            ImageIO.write(tmpImgtmpSideChain, "png", tmpOutputtmpSideChain);
            tmpCounter++;
        }

    }

    /**
     * Stores the linkers of the Schuffenhauer scaffold of all test molecules as images
     * The images are saved in the folder with the name of the test molecule. By removing the annotation, side chains from other scaffolds can also be output.
     * @throws Exception If anything goes wrong
     */
    @Test
    public void getLinkersTest() throws Exception {
        for (int tmpCount = 1; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate the SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            //tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
            //tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
            //Generate Linker
            List<IAtomContainer> tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
            /*Generate pictures of the Linkers*/
            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
            int tmpCounter = 1;
            for (IAtomContainer tmpLinker : tmpLinkers) {
                BufferedImage tmpImgtmpLinker = tmpGenerator.depict(tmpLinker).toImg();
                /*Save the picture*/
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName).mkdirs();
                File tmpOutputtmpLinker = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/Linker" + tmpCounter + ".png");
                ImageIO.write(tmpImgtmpLinker, "png", tmpOutputtmpLinker);
                tmpCounter++;
            }
        }
    }

    /**
     * Stores the linkers of the Schuffenhauer scaffold of all test molecules as images
     * The images are saved in the folder with the name of the test molecule. By removing the annotation, side chains from other scaffolds can also be output.
     * @throws Exception If anything goes wrong
     */
    @Test
    public void getLinkersWithoutHTest() throws Exception {
        for (int tmpCount = 1; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate the SchuffenhauerScaffold
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            //tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
            //tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
            //Generate Linker
            List<IAtomContainer> tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, false);
            /*Generate pictures of the Linkers*/
            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
            int tmpCounter = 1;
            for (IAtomContainer tmpLinker : tmpLinkers) {
                BufferedImage tmpImgtmpLinker = tmpGenerator.depict(tmpLinker).toImg();
                /*Save the picture*/
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName ).mkdirs();
                File tmpOutputtmpLinker = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/Linker" + tmpCounter + "WithoutH.png");
                ImageIO.write(tmpImgtmpLinker, "png", tmpOutputtmpLinker);
                tmpCounter++;
            }
        }
    }

    /**
     * Loads a molecule with a C=N linker from a SMILES and generates the corresponding linker for the different scaffolds.
     * @throws Exception If anything goes wrong
     */
    @Test
    public void getLinkersDoubleBondTest() throws Exception {
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C=C1CCCCC1/N=C/C2CC(C)CCC2C");
        //Generate the SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        /*Generate the linkers of the Schuffenhauer Scaffold*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.SCHUFFENHAUER_SCAFFOLD);
        List<IAtomContainer> tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        assertEquals("N=C",tmpSmilesGenerator.create(tmpLinkers.get(0)));
        /*Generate the linkers of the Murcko Framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
        tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        assertEquals("N=C",tmpSmilesGenerator.create(tmpLinkers.get(0)));
        /*Generate the linkers of the Elemental Wire Frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
        tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        assertEquals("NC",tmpSmilesGenerator.create(tmpLinkers.get(0)));
        /*Generate the linkers of the Beccari Basic Framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.BECCARI_BASIC_FRAMEWORK);
        tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        assertEquals("C=C",tmpSmilesGenerator.create(tmpLinkers.get(0)));
        /*Generate the linkers of the Beccari Basic Wire Frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.BECCARI_BASIC_WIRE_FRAME);
        tmpLinkers = tmpScaffoldGenerator.getLinkers(tmpMolecule, true);
        assertEquals("CC",tmpSmilesGenerator.create(tmpLinkers.get(0)));
    }

    //</editor-fold>

    //<editor-fold desc="Advanced method test">
    /**
     * Test of applyEnumerativeRemoval() with V2000 and V3000 mol files.
     * Loads the 12 Test(Test1.mol-Test12.mol) molfiles from the Resources folder and iteratively removes the terminal rings.
     * All generated molecules are saved as images in a subfolder of the scaffoldTestOutput folder.
     * The subfolder has the name of the input file.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getIterativeRemovalTest() throws Exception {
        for (int tmpCount = 1; tmpCount < 24; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            //Generate a list of molecules with iteratively removed terminal rings
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            List<IAtomContainer> tmpMolecules = tmpScaffoldGenerator.applyEnumerativeRemoval(tmpMolecule);
            int tmpCounter = 1;
            for (IAtomContainer tmpIterative : tmpMolecules) {
                /*Generate picture of the molecule*/
                DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                BufferedImage tmpImgRemove = tmpGenerator.depict(tmpIterative).toImg();
                /*Save the picture*/
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName ).mkdirs();
                File tmpOutputRemove = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/Iterative" + tmpCounter + ".png");
                ImageIO.write(tmpImgRemove, "png", tmpOutputRemove);
                tmpCounter++;
            }
        }
    }

    /**
     * Creates a ScaffoldTree from a V2000 or V3000 mol file and displays it as a network with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void graphStreamTreeTest() throws Exception {
        String tmpFileName = "Test11" ;
        //Load molecule from molfile
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
        //Generate a tree of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);
        /*Remove some nodes. Nodes can be removed from the non-root end.
        If nodes are removed in the middle of the tree, it cannot be displayed with Graphstream.*/
        System.out.println(tmpScaffoldTree.getAllNodes().size());
        //TreeNode tmpRemoveNode = (TreeNode) tmpScaffoldTree.getMatrixNode(3);
        //tmpScaffoldTree.removeNode(tmpRemoveNode);
        System.out.println(tmpScaffoldTree.getAllNodes().size());
        /*Print some further information*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldTree.getAllNodes()) {
            TreeNode tmpTestNode = (TreeNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            System.out.println("Origin" +tmpTestNode.getOriginSmilesList().get(0) + "Size" + tmpTestNode.getOriginSmilesList().size());
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                TreeNode tmpChildNode = (TreeNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        System.out.println("Max Lvl: " + tmpScaffoldTree.getMaxLevel());
        /*Display the Tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
    }

    /**
     * Creates a ScaffoldNetwork from a V2000 or V3000 mol file and displays it as a network with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void graphStreamNetworkTest() throws Exception {
        String tmpFileName = "Test11" ;
        //Load molecule from molfile
        IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
        //Generate a tree of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule);
        /*Remove some nodes. Nodes can be removed from the non-root end.
        If nodes are removed in the middle of the tree, it cannot be displayed with Graphstream.*/
        System.out.println(tmpScaffoldNetwork.getAllNodes().size());
        //tmpScaffoldNetwork.removeNode(tmpScaffoldNetwork.getMatrixNode(0));
        NetworkNode tmpRemoveNode = (NetworkNode) tmpScaffoldNetwork.getMatrixNode(9);
        tmpScaffoldNetwork.removeNode(tmpRemoveNode);
        System.out.println(tmpScaffoldNetwork.getAllNodes().size());
        /*Print some further information*/
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            System.out.println("Origin" +tmpTestNode.getOriginSmilesList().get(0) + "Size" + tmpTestNode.getOriginSmilesList().size());
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        System.out.println("Max Lvl: " + tmpScaffoldNetwork.getMaxLevel());
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
    }

    /**
     * Loads Figure 1 from the "Mining for Bioactive Scaffolds with Scaffold Networks"(2011) Paper by Varin et al.
     * Creates the Scaffold Networks of Ondasetron, Alosetron or Ramosetron. The result is visualised with GraphStream.
     * By selecting the corresponding lines, the molecule to be displayed can be chosen.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void getFigure1NetworkTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("CC1=NC=CN1CC2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Ondasetron
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("CC1=C(N=CN1)CN2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Alosetron
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("CN1C=C(C2=CC=CC=C21)C(=O)C3CCC4=C(C3)NC=N4");//Ramosetron
        //Generate a Network of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        /*Uncomment the molecule to display it*/
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule1);//Ondasetron
        //ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule2);//Alosetron
        //ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule3);//Ramosetron
        /*Print some further information*/
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            for(Object tmpOrigin : tmpTestNode.getOriginSmilesList()) {
                System.out.println("Origin: " + tmpOrigin);
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
    }

    /**
     * Creates different ScaffoldTrees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void mergeTreeTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        //IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        //IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("c2ccc1ccccc1c2");
        //IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c2ccc(Oc1ccccc1)cc2");
        //IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("O=c1[nH]c3cccc2cccc1c23");//3Rings
        //IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("O=C(Nc1ccccc1)c2ccccc2");
        //IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c2ccc1CCCc1c2");
        //IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c2ccc(c1ccccc1)cc2");
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        //IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("CC");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("c2ccc(C1NCNN1)cc2");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c2ccc1CCCc1c2");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c3ccc(C2NC1SCNN1N2)cc3");
        //Generate a tree of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        ScaffoldTree tmpScaffoldTree2 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule2);
        ScaffoldTree tmpScaffoldTree3 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule3);
        ScaffoldTree tmpScaffoldTree4 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule4);
        ScaffoldTree tmpScaffoldTree5 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule5);
        ScaffoldTree tmpScaffoldTree6 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule6);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree2);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree3);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree4);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree5);
        ScaffoldTree tmpOverlapScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree2);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree3);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree4);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree6);
        tmpScaffoldTree.mergeTree(tmpOverlapScaffoldTree);
        ScaffoldTree tmpUnfitScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);//Molecule does not fit
        System.out.println("Tree does not fit: " + tmpScaffoldTree.mergeTree(tmpUnfitScaffoldTree));
        IAtomContainer tmpRootMolecule = (IAtomContainer) tmpScaffoldTree.getRoot().getMolecule();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        System.out.println("I am Root: " + tmpSmilesGenerator.create(tmpRootMolecule));
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
    }

    /**
     * Creates different ScaffoldNetworks and merges them. The result is visualised with GraphStream.
     * A network is added here that has no connection to the rest of the network.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void mergeNetworkTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        /*Generate IAtomContainer from SMILES*/
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        //Molecule without connection to the network
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("C3CC1CC1CC4CCC2CC2CC34");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        //Generate a Network of molecules with iteratively removed terminal rings
        /*Generate Networks*/
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule1);
        ScaffoldNetwork tmpScaffoldNetwork2 = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule2);
        ScaffoldNetwork tmpScaffoldNetwork3 = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule3);
        ScaffoldNetwork tmpScaffoldNetwork4 = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule4);
        /*Merge Networks*/
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork2);
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork3);
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork4);
        /*Add edges and nodes*/
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            for(Object tmpOrigin : tmpTestNode.getOriginSmilesList()) {
                System.out.println("Origin: " + tmpOrigin);
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
    }

    /**
     * Creates different ScaffoldNetworks and merges them. The result is visualised with GraphStream.
     * A network is added here that has no connection to the rest of the network.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void generateEnumerativeForestTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        /*Generate IAtomContainer from SMILES*/
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        //Molecule without connection to the network
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("C3CC1CC1CC4CCC2CC2CC34");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        //Generate a Network of molecules with iteratively removed terminal rings
        /*Generate Networks*/
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpMoleculeList = new ArrayList<>();
        tmpMoleculeList.add(tmpMolecule1);
        tmpMoleculeList.add(tmpMolecule2);
        tmpMoleculeList.add(tmpMolecule3);
        tmpMoleculeList.add(tmpMolecule4);
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateEnumerativeForest(tmpMoleculeList);
        /*Add edges and nodes*/
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            for(Object tmpOrigin : tmpTestNode.getOriginSmilesList()) {
                System.out.println("Origin: " + tmpOrigin);
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
    }

    /**
     * Creates the different ScaffoldNetworks from Figure 1 from the "Mining for Bioactive Scaffolds with Scaffold Networks"(2011) Paper
     * by Varin et al. and merges them.
     * The networks of Ondasetron and Alosetron are merged.
     * The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void mergeFigure1NetworkTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("CC1=NC=CN1CC2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Ondasetron
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("CC1=C(N=CN1)CN2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Alosetron
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("CN1C=C(C2=CC=CC=C21)C(=O)C3CCC4=C(C3)NC=N4");//Ramosetron
        /*Generate a network of molecules with iteratively removed terminal rings*/
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule1);
        ScaffoldNetwork tmpScaffoldNetwork2 = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule2);
        ScaffoldNetwork tmpScaffoldNetwork3 = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule3);
        /*Merge the networks*/
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork2);
        //Uncomment if ramosetron should also be added to the network
        //tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork3);//Ramosetron
        /*Print some further information*/
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            for(Object tmpOrigin : tmpTestNode.getOriginSmilesList()) {
                System.out.println("Origin: " + tmpOrigin);
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
    }

    /**
     * Creates different ScaffoldTrees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void mergeTreeOriginTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        //IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        //IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("c2ccc1ccccc1c2");
        //IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c2ccc(Oc1ccccc1)cc2");
        //IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("O=c1[nH]c3cccc2cccc1c23");//3Rings
        //IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("O=C(Nc1ccccc1)c2ccccc2");
        //IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c2ccc1CCCc1c2");
        //IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c2ccc(c1ccccc1)cc2");
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        //IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("C1=CC=C(C=C1)C4NC3SC(C2=CC(=CC=C2)[Br])NN3N4");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("c2ccc(C1NCNN1)cc2");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("C1=CC=C(C=C1)C3NNC(C2=CC=CC=C2[Br])N3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c2ccc1CCCc1c2");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("C1=CC=C(C=C1)C3NC2SC(NN2N3)[Br]");
        //Generate a tree of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        ScaffoldTree tmpScaffoldTree2 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule2);
        ScaffoldTree tmpScaffoldTree3 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule3);
        ScaffoldTree tmpScaffoldTree4 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule4);
        ScaffoldTree tmpScaffoldTree5 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule5);
        ScaffoldTree tmpScaffoldTree6 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule6);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree2);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree3);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree4);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree5);
        ScaffoldTree tmpOverlapScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree2);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree3);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree4);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree6);
        tmpScaffoldTree.mergeTree(tmpOverlapScaffoldTree);
        ScaffoldTree tmpUnfitScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);//Molecule does not fit
        System.out.println("Tree does not fit: " + tmpScaffoldTree.mergeTree(tmpUnfitScaffoldTree));
        IAtomContainer tmpRootMolecule = (IAtomContainer) tmpScaffoldTree.getRoot().getMolecule();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        System.out.println("I am Root: " + tmpSmilesGenerator.create(tmpRootMolecule));
        System.out.println("Number of Origins: " + tmpScaffoldTree.getRoot().getOriginCount());
        for(ScaffoldNodeBase tmpTreeNodeBase : tmpScaffoldTree.getAllNodes()) {
            TreeNode tmpTreeNode = (TreeNode) tmpTreeNodeBase;
            System.out.println("---Node: " + tmpSmilesGenerator.create((IAtomContainer) tmpTreeNode.getMolecule()));
            for(Object tmpSmiles : tmpTreeNode.getOriginSmilesList()) {
                System.out.println("Origin of the Node: " + tmpSmiles);
            }
        }
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
    }

    /**
     * Creates different ScaffoldTrees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void mergeMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        /*
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("c2ccc1ccccc1c2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c2ccc(Oc1ccccc1)cc2");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("O=c1[nH]c3cccc2cccc1c23");//3Rings
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("O=C(Nc1ccccc1)c2ccccc2");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c2ccc1CCCc1c2");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c2ccc(c1ccccc1)cc2");
        */
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("c1ncc2c(n1)SC3CCCCC23");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c1ncc2c(n1)SC3CCCC23");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("c2ccc1ncncc1c2");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc2nc(CC1NCNCN1)ncc2c3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c1ccc3c(c1)oc2cncnc23");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c1cnc3c(c1)oc2cncnc23");
        IAtomContainer tmpMolecule7 = tmpParser.parseSmiles("c3ccc(N2NCc1cncnc12)cc3");
        IAtomContainer tmpMolecule8 = tmpParser.parseSmiles("c2cnc(N1CCCCC1)nc2");
        IAtomContainer tmpMolecule9 = tmpParser.parseSmiles("c2ncc1NCNc1n2");
        IAtomContainer tmpMolecule10 = tmpParser.parseSmiles("c1ccc2c(c1)[nH]c3ncncc23");
        //Generate a tree of molecules with iteratively removed terminal rings

        List<IAtomContainer> tmpTreeList = new ArrayList<>();
        tmpTreeList.add(tmpMolecule);
        tmpTreeList.add(tmpMolecule1);
        tmpTreeList.add(tmpMolecule2);
        tmpTreeList.add(tmpMolecule3);
        tmpTreeList.add(tmpMolecule4);
        tmpTreeList.add(tmpMolecule5);
        tmpTreeList.add(tmpMolecule6);
        tmpTreeList.add(tmpMolecule7);
        tmpTreeList.add(tmpMolecule8);
        tmpTreeList.add(tmpMolecule9);
        tmpTreeList.add(tmpMolecule10);
        List<ScaffoldTree> tmpFinalForest = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTreeList);
        System.out.println("Forest size: " + tmpFinalForest.size());
        ScaffoldTree tmpScaffoldTree = tmpFinalForest.get(1);
        IAtomContainer tmpRootMolecule = (IAtomContainer) tmpScaffoldTree.getRoot().getMolecule();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        System.out.println("I am Root: " + tmpSmilesGenerator.create(tmpRootMolecule));
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
    }

    /**
     * Creates different ScaffoldTrees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void mergeAllTestMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpTestMoleculeList = new ArrayList<>();
        for (int tmpCount = 1; tmpCount < 24; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpTestMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            tmpTestMoleculeList.add(tmpTestMolecule);
        }
        List<ScaffoldTree> tmpTestTreeList = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTestMoleculeList);
        System.out.println("Number of molecules: " + tmpTestMoleculeList.size());
        System.out.println("Number of trees: " + tmpTestTreeList.size());
        ScaffoldTree tmpScaffoldTree = tmpTestTreeList.get(2);
        System.out.println("Origin SMILES: " + tmpScaffoldTree.getRoot().getOriginSmilesList());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldTree tmpTestTree : tmpTestTreeList) {
            System.out.println("Number of Nodes:" + tmpTestTree.getAllNodes().size());
            System.out.println("Root:" + tmpSmilesGenerator.create((IAtomContainer) tmpTestTree.getRoot().getMolecule()));
        }
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
    }

    /**
     * Creates 25000 different ScaffoldTrees from COCONUT molecules and merges them.
     * The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void mergeCOCONUTMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpTestMoleculeList = new ArrayList<>();
        /*Loading and reading the library*/
        File tmpResourcesDirectory = new File("src/test/resources/COCONUT_DB.sdf");
        IteratingSDFReader tmpReader = new IteratingSDFReader( new FileInputStream(tmpResourcesDirectory), DefaultChemObjectBuilder.getInstance());
        int tmpAllCounter = 0;
        int tmpCounter = 0;
        while (tmpReader.hasNext()) {
            tmpAllCounter++;
            tmpCounter++;
            if(tmpCounter < 25000) {
                continue;
            }
            IAtomContainer tmpMolecule = (IAtomContainer) tmpReader.next();
            tmpTestMoleculeList.add(tmpMolecule);
            if(tmpAllCounter == 50000) {
                break;
            }
        }
        System.out.println("Number of molecules: " + tmpTestMoleculeList.size());
        List<ScaffoldTree> tmpTestTreeList = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTestMoleculeList);
        System.out.println("Number of trees: " + tmpTestTreeList.size());
        ScaffoldTree tmpScaffoldTree = tmpTestTreeList.get(10);
        System.out.println("Origin SMILES: " + tmpScaffoldTree.getRoot().getOriginSmilesList());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        tmpCounter = 0;
        for(ScaffoldTree tmpTestTree : tmpTestTreeList) {
            System.out.println(tmpCounter + "Root:" + tmpSmilesGenerator.create((IAtomContainer) tmpTestTree.getRoot().getMolecule()));
            System.out.println("Number of Nodes:" + tmpTestTree.getAllNodes().size());
            tmpCounter++;
        }
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
    }

    /**
     * Loads a molecule with two stereochemical information from a SMILES. Outputs its fragments.
     * Shows that one piece of information is retained even though the other has already been removed.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void stereoFragmentationTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setSmilesGeneratorSetting(new SmilesGenerator(SmiFlavor.Isomeric));
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("F\\C(=C(\\F)CC1CCCCC1)CC2CCCCC2");
        //tmpMolecule = tmpParser.parseSmiles("C1CC[C@]2(OCC=CC2)NC1");
        tmpMolecule = tmpParser.parseSmiles("C8CCCC(/C(=C(C1CCCNCCC1)\\C6CCCCC(C5CCCC(C/C(CC2CCNCC2)=C(CC3CCCCC3)/CC4CCNCC4)C5)CC6)C7CCCCCNC7)CCC8");
        //tmpMolecule = tmpParser.parseSmiles("C1CC[C@@]2(OCC=CC2)NC1");
        List<IAtomContainer> tmpFragmentList = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Isomeric);
        for(IAtomContainer tmpFragment : tmpFragmentList) {
            System.out.println(tmpSmilesGenerator.create(tmpFragment));
        }
    }

    /**
     * Loads two stereoisomers as SMILES and joins them as a tree. Since the SMILESGenerator setting is "Isomeric",
     * the stereochemistry is kept in consideration and the two molecules are represented in the tree as two different ones.
     *
     * The structure is similar to the method "mergeNonStereoMoleculesToForestTest()" except for the setting.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void mergeStereoMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpTestMoleculeList = new ArrayList<>();
        /*Loading and reading the library*/
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C4CCC(C/C(CC1CC[F+]CC1)=C(CC2CCCCC2)\\CC3CC[F+]CC3)CC4");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C4CCC(C/C(CC1CC[F+]CC1)=C(CC2CCCCC2)/CC3CC[F+]CC3)CC4");
        tmpTestMoleculeList.add(tmpMolecule);
        tmpTestMoleculeList.add(tmpMolecule1);
        tmpScaffoldGenerator.setSmilesGeneratorSetting(new SmilesGenerator(SmiFlavor.Isomeric));
        List<ScaffoldTree> tmpTestTreeList = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTestMoleculeList);
        System.out.println("Number of molecules: " + tmpTestMoleculeList.size());
        System.out.println("Number of trees: " + tmpTestTreeList.size());
        ScaffoldTree tmpScaffoldTree = tmpTestTreeList.get(0);
        System.out.println("Origin SMILES: " + tmpScaffoldTree.getRoot().getOriginSmilesList());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Isomeric);
        for(ScaffoldTree tmpTestTree : tmpTestTreeList) {
            System.out.println("Number of Nodes:" + tmpTestTree.getAllNodes().size());
            tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
            IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            System.out.println("Root:" + tmpSmilesGenerator.create((IAtomContainer) tmpTestTree.getRoot().getMolecule()));
            for(ScaffoldNodeBase tmpNodeBase : tmpTestTree.getAllNodes()) {
                TreeNode tmpNode = (TreeNode) tmpNodeBase;
                System.out.println("Molecules:" + tmpSmilesGenerator.create((IAtomContainer) tmpNode.getMolecule()));
            }
        }
        assertEquals(5, tmpScaffoldTree.getAllNodes().size());
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
    }

    /**
     * Loads two stereoisomers as SMILES and joins them as a tree. Since the SMILESGenerator setting is "Unique",
     * the stereochemistry is ignored and the two molecules are represented as one in the tree.
     *
     * The structure is similar to the method "mergeStereoMoleculesToForestTest()" except for the setting.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void mergeNonStereoMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpTestMoleculeList = new ArrayList<>();
        /*Loading and reading the library*/
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C4CCC(C/C(CC1CC[F+]CC1)=C(CC2CCCCC2)\\CC3CC[F+]CC3)CC4");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C4CCC(C/C(CC1CC[F+]CC1)=C(CC2CCCCC2)/CC3CC[F+]CC3)CC4");
        tmpTestMoleculeList.add(tmpMolecule);
        tmpTestMoleculeList.add(tmpMolecule1);
        tmpScaffoldGenerator.setSmilesGeneratorSetting(new SmilesGenerator(SmiFlavor.Unique));
        List<ScaffoldTree> tmpTestTreeList = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTestMoleculeList);
        System.out.println("Number of molecules: " + tmpTestMoleculeList.size());
        System.out.println("Number of trees: " + tmpTestTreeList.size());
        ScaffoldTree tmpScaffoldTree = tmpTestTreeList.get(0);
        System.out.println("Origin SMILES: " + tmpScaffoldTree.getRoot().getOriginSmilesList());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldTree tmpTestTree : tmpTestTreeList) {
            System.out.println("Number of Nodes:" + tmpTestTree.getAllNodes().size());
            tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldModeOption.MURCKO_FRAMEWORK);
            IAtomContainer tmpScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
            System.out.println("Root:" + tmpSmilesGenerator.create((IAtomContainer) tmpTestTree.getRoot().getMolecule()));
            System.out.println("Scaffold:" + tmpSmilesGenerator.create(tmpScaffold));
            for(ScaffoldNodeBase tmpNodeBase : tmpTestTree.getAllNodes()) {
                TreeNode tmpNode = (TreeNode) tmpNodeBase;
                System.out.println("Molecules:" + tmpSmilesGenerator.create((IAtomContainer) tmpNode.getMolecule()));
            }
        }
        assertEquals(4, tmpScaffoldTree.getAllNodes().size());
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
    }
    //</editor-fold>

    //<editor-fold desc="Schuffenhauer rules tests">
    /**
     * Test oftmpScaffoldGenerator.applySchuffenhauerRules() with V2000 and V3000 mol files.
     * Loads the Test(Test1.mol-Test21.mol) molfiles from the Resources folder and creates the SchuffenhauerScaffolds with getScaffold().
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * The subfolder has the name of the input file.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void applySchuffenhauerRulesTest() throws Exception {
        for (int tmpCount = 1; tmpCount < 23; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpMolecule = this.loadMolFile("src/test/resources/" + tmpFileName + ".mol");
            /*Generate picture of molecule*/
            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
            ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
            List<IAtomContainer> tmpSchuffenhauerFragments = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
            int tmpCounter = 0;
            for(IAtomContainer tmpFragment : tmpSchuffenhauerFragments) {
                tmpCounter++;
                /*Generate picture*/
                BufferedImage tmpImgFragment = tmpGenerator.depict(tmpFragment).toImg();
                /*Save the picture*/
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/SchuffenhauerRules").mkdirs();
                File tmpOutputFragment = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/TestMolecules/" + tmpFileName + "/SchuffenhauerRules/Fragment"+  tmpCounter + ".png");
                ImageIO.write(tmpImgFragment, "png", tmpOutputFragment);
            }
        }
    }

    /**
     * Test of ScaffoldGenerator.applySchuffenhauerRules() with SMILES.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * The subfolder has the name of the input file.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void applySchuffenhauerRulesSMILESTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C5CCCCCCCCCCCCCC1NC1CCCCCCCCCCCCCC(C3CC2CC2C4NC34)CC5");//Original
        //tmpMolecule = tmpParser.parseSmiles("C6CCC(CCC4CC(CC1CCCC1)C(C2CCCC2)C5C(C3CCCC3)CCC45)C6");
        //tmpMolecule = tmpParser.parseSmiles("OCC1OC2OC3C(O)C(O)C(OC3CO)OC4C(O)C(O)C(OC4CO)OC5C(O)C(O)C(OC5CO)OC6C(O)C(O)C(OC6CO)OC7C(O)C(O)C(OC7CO)OC8C(O)C(O)C(OC8CO)OC9C(O)C(O)C(OC9CO)OC%10C(O)C(O)C(OC%10CO)OC%11C(O)C(O)C(OC%11CO)OC%12C(O)C(O)C(OC%12CO)OC1C(O)C2O");
        //tmpMolecule = tmpParser.parseSmiles("C2=C1[F+]C1CC2");
        //tmpMolecule = tmpParser.parseSmiles("C2CCC1SC1C2");
        //tmpMolecule = tmpParser.parseSmiles("C2CCC(C1C[Br+]1)C2");
        //tmpMolecule = tmpParser.parseSmiles("C2=C1[I+]C1CCC2");
        //tmpMolecule = tmpParser.parseSmiles("C2=C1[I+]C1=CCC2");
        //tmpMolecule = tmpParser.parseSmiles("C2CC(C1C[Br+]1)CC2C3C[F+]3");
        //tmpMolecule = tmpParser.parseSmiles("[Cl+]2C3C1[I+]C1C4[Cl+]C234");
        //tmpMolecule = tmpParser.parseSmiles("C=1C=CC=2C(C1)=C3C=CC4=C5C=CC=CC5=C6C=CC2C3=C46");
        tmpMolecule = tmpParser.parseSmiles("c2ccc1[nH]ccc1c2");
        tmpMolecule = tmpParser.parseSmiles("O=C4C(=O)C3CC2C1CNNC1NC2C3C4=O");
        tmpMolecule= tmpParser.parseSmiles("C1NOCC2CSNCC12");
        tmpMolecule = tmpParser.parseSmiles("C2CC1NOCC1C3NSCC23");
        tmpMolecule = tmpParser.parseSmiles("C2NNNC3CC1SNNCC1CC23");
        tmpMolecule = tmpParser.parseSmiles("O=C2CC(=[Br+])C1CCCCCC1CC2=P");
        //tmpMolecule = tmpParser.parseSmiles("CCN(C1=CC=CC(=C1)C2=CC=NC3=C(C=NN23)C#N)C(=O)C");
        tmpMolecule = tmpParser.parseSmiles("N1=C2C=CC=CC2=NC3=C1C(=NN3C=4C=CC=CC4)N");
        tmpMolecule = tmpParser.parseSmiles("N1=CC=2C(=NN(C=3C=CC=CC3)S2N1C)C=4C=CC=CC4");
        tmpMolecule = tmpParser.parseSmiles("c1ccc5c(c1)c2ccccc2C6c3ccccc3c4ccccc4C56");
        tmpMolecule = tmpParser.parseSmiles("N=1C=CN(C1)C2=NC=CO2");
        tmpMolecule = tmpParser.parseSmiles("O=CN1C=CC2=CC=NN21");
        tmpMolecule = tmpParser.parseSmiles("N=1C=2C=CC=CC2N=C3C1C=4C=CC=C(C34)C");
        //tmpMolecule = tmpParser.parseSmiles("c1c[nH]cn1");
        //tmpMolecule = tmpParser.parseSmiles("C5CCN(CCC3CN(CCC1CCNC1)CN(CCN2CCCC2)C3CCC4CCCN4)C5"); //Rule 12
        //tmpMolecule = tmpParser.parseSmiles("C5CCN(C3CN(C1CCCN1)C(C2CCNC2)N3N4CCCC4)C5"); //Rule 12
        //tmpMolecule = tmpParser.parseSmiles("[I+]=C(NC1CCCN1)C4C(CCN2CCCC2)CN(CCC3CCNC3)CN4CCN5CCCC5"); //Rule 12
        tmpMolecule = tmpParser.parseSmiles("O=C(O)C1CCC(=CO)C(CCCNC2=CC=C(C=[NH+]2)C=3C=CC=C(C3)CC4=C5C=CC=CC5=CC6C4=CC78C=CCC9(C)C(O)CCC6(C%10=C(C7)C%11%12CC(O)C%131C=CC%14C%15%16CCC%14%12C(C=CC%16CC(CC=%17C=CC=CC%17CCCCC)C%15)CC(C%10)C%13%11C)C89)C%18CCCCC%18");
        tmpMolecule = tmpParser.parseSmiles("[H]OC(=O)C1([H])C23C([H])=C([H])C4([H])C56C7(C8=C(C9%10C%11([H])C(C(=C%12C(C([H])=C([H])C([H])=C%12[H])=C%11[H])C([H])([H])C%13=C([H])C(C=%14C([H])=C([H])C(N([H])C([H])([H])C([H])([H])C([H])([H])C([H])(C(=C([H])O[H])C([H])([H])C1([H])[H])C%15([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C%15([H])[H])=[N+]([H])C%14[H])=C([H])C([H])=C%13[H])=C([H])C%16(C([H])=C([H])C([H])([H])C(C([H])(O[H])C([H])([H])C9([H])[H])(C%16%10[H])C([H])([H])[H])C8([H])[H])C([H])([H])C([H])(C72C([H])([H])[H])C([H])([H])C6([H])C([H])=C([H])C%17([H])C4(C([H])([H])C([H])(C([H])([H])C=%18C(=C([H])C([H])=C([H])C%18[H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])[H])C%17([H])[H])C([H])([H])C5([H])[H])C([H])([H])C3([H])O[H]");
        tmpMolecule = tmpParser.parseSmiles("S=P12N=P3(OC=4C=CC(C=NN(C)P(=S)(C=5C=CC=CC5)N(N=CC=6C=CC(OP(=NP(=S)(OC=7C=CC(C=NN(C)P(=S)(C=8C=CC=CC8)N(N=CC=9C=CC(O1)=CC9)C)=CC7)OC=%10C=CC(C=NN(C)P(=S)(C=%11C=CC=CC%11)N(N=CC=%12C=CC(O2)=CC%12)C)=CC%10)(OC=%13C=CC(C=NN(C)P(=S)(C=%14C=CC=CC%14)N(N=CC=%15C=CC(O3)=CC%15)C)=CC%13)C=%16C=CC=CC%16)=CC6)C)=CC4)C=%17C=CC=CC%17");
        //tmpMolecule = tmpParser.parseSmiles("[H]OC(=O)C1([H])C2([H])C3(C4(C5=C(C67C8([H])C=9C(=C%10C(C([H])=C([H])C([H])=C%10[H])=C8[H])C([H])(C%11=C([H])C(C=%12C([H])=C([H])C(N([H])C([H])([H])C([H])([H])C([H])([H])C([H])(C(=C([H])O[H])C([H])([H])C1([H])[H])C%13([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C%13([H])[H])=[N+]([H])C%12[H])=C([H])C([H])=C%11[H])C([H])([H])C#CC([H])([H])C%14(C([H])(O[H])C([H])([H])C6([H])[H])C7([H])C(C9[H])(C([H])=C([H])C%14([H])[H])C5([H])[H])C([H])([H])C3([H])C([H])([H])C%15([H])C4%16C([H])([H])C%17(C([H])([H])C([H])(C([H])(C([H])([H])C=%18C([H])=C([H])C([H])=C([H])C%18[H])C%17([H])[H])C([H])([H])SSC%15([H])[H])C([H])([H])C%16([H])[H])C([H])([H])C2([H])O[H])C([H])([H])[H]");
        //tmpMolecule = tmpParser.parseSmiles("[H]OC(=O)C1([H])C2([H])C3(C(C4=C(C56C7([H])C(C(=C8C(C([H])=C([H])C([H])=C8[H])=C7[H])C([H])([H])C9=C([H])C(C=%10C([H])=C([H])C(N([H])C([H])([H])C([H])([H])C([H])([H])C([H])(C(=C([H])[H])C([H])([H])C1([H])[H])C%11([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C%11([H])[H])=[N+]([H])C%10[H])=C([H])C([H])=C9[H])=C([H])C%12(C(=C([H])C([H])([H])C(C([H])(O[H])C([H])([H])C5([H])[H])(C%126[H])C([H])([H])[H])C([H])([H])C([H])(C([H])([H])[H])C([H])([H])[H])C4([H])[H])C([H])([H])C3([H])[H])(C%13([H])C([H])([H])C%14([H])C([H])(C([H])(C([H])([H])C=%15C([H])=C([H])C([H])=C([H])C%15[H])C([H])([H])C([H])([H])C%14([H])[H])C([H])([H])C%13([H])[H])C([H])([H])C2([H])O[H])C([H])([H])[H]");
        //tmpMolecule = tmpParser.parseSmiles("[H]C1([H])C2=C(SS=3SC(SC([H])([H])[H])=C(C23)C([H])([H])C1([H])[H])SC([H])([H])[H]");
        tmpMolecule = tmpParser.parseSmiles("O=CC(OC(=O)C1=CC(O)=C(O)C(O)=C1OC=2C=C3C(=O)[O+]=C4C(O)=C(O)C=C5C(=O)[O+]=C(C2O)C3=C45)C(OC(=O)C6=CC(O)=C(O)C(O)=C6)C7OC(=O)C8=CC(O)=C(O)C(O)=C8C=9C(O)=C(O)C(O)=CC9C(=O)OCC7O");
        tmpMolecule = tmpParser.parseSmiles("[H]OC=1C([H])=C2C(C=3C([H])=C(C([H])=C([H])C3[H])C([H])([H])N([H])C([H])([H])C([H])([H])C=4C([H])=C(C([H])=C(C4[H])C([H])([H])N([H])C([H])([H])C([H])([H])C([H])([H])[H])C5([H])N([H])C([H])([H])C(O[H])(C([H])([H])C#CC([H])(C([H])([H])O[H])C([H])([H])C6([H])C5([H])C7([H])C(C8([H])C([H])(C([H])([H])N([H])C([H])(N([H])C([H])([H])[H])C8([H])[H])C([H])([H])C7([H])[H])(C([H])([H])C([H])=C9OC(=O)C%10=C9C([H])([H])C([H])([H])C%11([H])C%10([H])C%12%13C(C(=O)OC%12=C([H])C([H])(C([H])([H])C=%14C([H])=C([H])C([H])=C([H])C%14[H])C([H])([H])C([H])([H])[H])=C2C%11([H])C([H])([H])C%13([H])[H])C6([H])[H])C([H])([H])[H])=C([H])C1[H]");
        tmpMolecule = tmpParser.parseSmiles("CC1(C(=O)C(=C(O1)C2=CC=C(C=C2)S(=O)(=O)N)C3=CC(=CC=C3)F)C");
        /*Generate picture of molecule*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        List<IAtomContainer> tmpSchuffenhauerFragments = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        int tmpCounter = 0;
        for(IAtomContainer tmpFragment : tmpSchuffenhauerFragments) {
            MurckoFragmenter tmpMurckoFragmenter = new MurckoFragmenter(true,1);
            tmpMurckoFragmenter.setComputeRingFragments(false);
            tmpFragment = tmpMurckoFragmenter.scaffold(tmpFragment);
            tmpFragment = AtomContainerManipulator.anonymise(tmpFragment);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpFragment);
            CDKHydrogenAdder.getInstance(tmpFragment.getBuilder()).addImplicitHydrogens(tmpFragment);
            tmpCounter++;
            /*Generate picture*/
            BufferedImage tmpImgFragment = tmpGenerator.depict(tmpFragment).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/RulesSMILESTest").mkdirs();
            File tmpOutputFragment = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/RulesSMILESTest/Fragment" + tmpCounter + ".png");
            ImageIO.write(tmpImgFragment, "png", tmpOutputFragment);
        }
    }
    /**
     * Test of ScaffoldGenerator.getScaffold() with SMILES.
     * Loads Scheme 1 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES and generates SchuffenhauerScaffold.
     * Flucloxacillin is generated from the SMILES and all terminal side chains are removed. Rings, linkers and double bonds on these structures are obtained.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme1Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        /*Generate picture of the Original molecule*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        BufferedImage tmpImgOriginal = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture of the original*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme1").mkdirs();
        File tmpOutputOriginal = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme1/Original.png");
        ImageIO.write(tmpImgOriginal, "png" ,tmpOutputOriginal);
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme1").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme1/Schuffenhauer.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C(NC1C(=O)N2CCSC21)C3=CON=C3C=4C=CC=CC4", tmpSmilesGenerator.create(tmpSchuffenhauerSMILES));
    }

    /**
     * Loads Scheme 2b from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Adamantane is generated from the SMILES and it is checked whether rings can be removed. This should not be the case.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme2bTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1C2CC3CC1CC(C2)C3");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme2b").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme2b/Schuffenhauer.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        //Get rings
        List<IAtomContainer> tmpRings =tmpScaffoldGenerator.getRingsInternal(tmpSchuffenhauerScaffold, true);
        int tmpCounter = 0;
        for(IAtomContainer tmpRing : tmpRings) {
            boolean tmpIsRingRemovable =tmpScaffoldGenerator.isRingRemovable(tmpSchuffenhauerScaffold, tmpRings, tmpRing);
            /*Remove rings*/
            IAtomContainer tmpRemovedSchuff =tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, true, tmpRing);
            /*Generate picture*/
            BufferedImage tmpImgRemove = tmpGenerator.depict(tmpRemovedSchuff).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme2b").mkdirs();
            File tmpOutputRemove = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme2b/RingRemovable" + tmpIsRingRemovable + tmpCounter + ".png");
            ImageIO.write(tmpImgRemove, "png", tmpOutputRemove);
            tmpCounter++;
            /*Check boolean*/
            assertEquals(false, tmpIsRingRemovable);
        }
    }

    /**
     * Loads Scheme 3a from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * A molecule consisting of two rings is generated from a SMILES.
     * One of these rings is aromatic and has to be removed.
     * At the point where this aromatic ring was bound to the other ring, a double bond should now be formed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme3aTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("c2ccc1CNCCc1c2");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme3a").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme3a/Schuffenhauer.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        //Get rings
        List<IAtomContainer> tmpRings =tmpScaffoldGenerator.getRingsInternal(tmpSchuffenhauerScaffold, true);
        //Remove Ring
        IAtomContainer tmpRemovedSchuff =tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, true, tmpRings.get(1));
        /*Generate picture*/
        BufferedImage tmpImgRemove = tmpGenerator.depict(tmpRemovedSchuff).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme3a").mkdirs();
        File tmpOutputRemove = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme3a/RemovedRing.png");
        ImageIO.write(tmpImgRemove, "png", tmpOutputRemove);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1=CCCNC1", tmpSmilesGenerator.create(tmpRemovedSchuff));
    }

    /**
     * Loads Scheme 3b from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * A molecule consisting of three rings is generated from a SMILES. One of these rings is aromatic.
     * It is tested whether this aromatic ring can be removed. This should not be the case.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme3bTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("c1cc2CCCc3c[nH]c(c1)c23");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme3b").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme3b/Schuffenhauer.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        //Get rings
        List<IAtomContainer> tmpRings =tmpScaffoldGenerator.getRingsInternal(tmpSchuffenhauerScaffold, true);
        int tmpCounter = 0;
        for(IAtomContainer tmpRing : tmpRings) {
            boolean tmpIsRemovable =tmpScaffoldGenerator.isRingRemovable(tmpRing, tmpRings, tmpSchuffenhauerScaffold);
            /*Remove rings*/
            IAtomContainer tmpRemovedSchuff =tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, true, tmpRing);
            /*Generate picture*/
            BufferedImage tmpImgRemove = tmpGenerator.depict(tmpRemovedSchuff).toImg();
            /*Save the picture*/
            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme3b").mkdirs();
            File tmpOutputRemove = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme3b/IsRingRemovable_" + tmpIsRemovable + tmpCounter + ".png");
            ImageIO.write(tmpImgRemove, "png", tmpOutputRemove);
            tmpCounter++;
        }
        /*Check booleans*/
        assertEquals(false, tmpScaffoldGenerator.isRingRemovable(tmpRings.get(0), tmpRings, tmpSchuffenhauerScaffold));
        assertEquals(false, tmpScaffoldGenerator.isRingRemovable(tmpRings.get(1), tmpRings, tmpSchuffenhauerScaffold));
        assertEquals(true, tmpScaffoldGenerator.isRingRemovable(tmpRings.get(2), tmpRings, tmpSchuffenhauerScaffold));
    }

    /**
     * Loads Scheme 4 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Epothilone A is generated from a SMILES and the ring consisting of 3 atoms is removed.
     * The removal of this hetero ring should result in a double bond at the removed position.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme4Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1CCCC2C(O2)CC(OC(=O)CC(C(C(=O)C(C1O)C)(C)C)O)C(=CC3=CSC(=N3)C)C");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme4").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme4/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        //Get rings
        List<IAtomContainer> tmpRings =tmpScaffoldGenerator.getRingsInternal(tmpSchuffenhauerScaffold, true);
        /*Generate picture of the SchuffenhauerRuleOne*/
        List<IAtomContainer> tmpRuleOne = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRuleOne = tmpGenerator.depict(tmpRuleOne.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme4").mkdirs();
        File tmpOutputRuleOne = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme4/RuleOne.png");
        ImageIO.write(tmpImgRuleOne, "png" ,tmpOutputRuleOne);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1OC(C=CC=2N=CSC2)CC=CCCCCCCC(=O)CCC1", tmpSmilesGenerator.create(tmpRuleOne.get(1)));
    }

    /**
     * Loads Scheme 4 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Epothilone A is generated from a SMILES and the ring consisting of 3 atoms is removed.
     * The removal of this hetero ring should result in a double bond at the removed position.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme4Rule1OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1CCCC2C(O2)CC(OC(=O)CC(C(C(=O)C(C1O)C)(C)C)O)C(=CC3=CSC(=N3)C)C");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme4").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme4/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerRuleOne*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleOne(this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRuleOne = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme4").mkdirs();
        File tmpOutputRuleOne = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme4/RuleOneOnly.png");
        ImageIO.write(tmpImgRuleOne, "png" ,tmpOutputRuleOne);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1OC(C=CC=2N=CSC2)CC=CCCCCCCC(=O)CCC1", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme5 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Seglitide is generated from a SMILES and the two single rings connected via linker are removed.
     * Then, according to the second rule, the aromatic 6 ring is removed to obtain the macroring.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme5Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1C(=O)NC(C(=O)NC(C(=O)NC(C(=O)NC(C(=O)NC(C(=O)N1C)CC2=CC=CC=C2)C(C)C)CCCCN)CC3=CNC4=CC=CC=C43)CC5=CC=C(C=C5)O");//Original
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        //Get rings
        List<IAtomContainer> tmpRings =tmpScaffoldGenerator.getRingsInternal(tmpSchuffenhauerScaffold, true).subList(1,3);
        tmpSchuffenhauerScaffold = tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, true, tmpRings.get(0));
        tmpSchuffenhauerScaffold = tmpScaffoldGenerator.removeRing(tmpSchuffenhauerScaffold, true, tmpRings.get(1));
        /*Generate picture*/
        BufferedImage tmpImgRemove = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5").mkdirs();
        File tmpOutputRemove = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5/Modified" + ".png");
        ImageIO.write(tmpImgRemove, "png", tmpOutputRemove);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffold);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5/RuleTwo.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1NCC(=O)NCC(=O)NC(C(=O)NCC(=O)NCC(=O)NC1)CC=2C=CNC2", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads Scheme5 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Seglitide from which the single rings have already been removed is generated from a SMILES.
     * Then, according to the second rule, the aromatic 6 ring is removed to obtain the macroring.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme5Rule2OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O=C1NCC(=O)NC(C(=O)NCC(=O)NC(C(=O)NC(C(=O)NC1)CC2=CNC=3C=CC=CC32)C)C");//Original
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleTwo(this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme5/RuleTwoOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1NCC(=O)NCC(=O)NC(C(=O)NCC(=O)NCC(=O)NC1)CC=2C=CNC2", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 6  from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Flucloxacillin is generated from a SMILES and the ring consisting of 6 atoms is removed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme6Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");//Original
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme6").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme6/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme6").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme6/RuleThree.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C(NC1C(=O)N2CCSC21)C=3C=NOC3", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads Scheme 6  from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Flucloxacillin is generated from a SMILES and the ring consisting of 6 atoms is removed.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme6Rule3OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme6").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme6/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleThree(tmpSchuffenhauerScaffold, this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme6").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme6/RuleThreeOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C(NC1C(=O)N2CCSC21)C=3C=NOC3", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 7 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Pentazocine is generated from a SMILES and the aromatic ring consisting of 6 atoms is removed.
     * A double bond is inserted at the point where it was removed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme7Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1C2CC3=C(C1(CCN2CC=C(C)C)C)C=C(C=C3)O");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme7").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme7/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme7").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme7/RuleFour.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1=CC2CCNC(C1)C2", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads Scheme 7 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Pentazocine is generated from a SMILES and the aromatic ring consisting of 6 atoms is removed.
     * A double bond is inserted at the point where it was removed.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme7Rule4and5OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1C2CC3=C(C1(CCN2CC=C(C)C)C)C=C(C=C3)O");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme7").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme7/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleFourAndFive(tmpSchuffenhauerScaffold, this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme7").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme7/RuleFourOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1=CC2CCNC(C1)C2", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 8 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Sophocarpin is generated from a SMILES and the ring that only has an overlapping bond is removed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme8Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CC2CN3C(CC=CC3=O)C4C2N(C1)CCC4");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme8").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme8/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme8").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme8/RuleFour.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("N1CC2CCCN3CCCC(C1)C32", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads Scheme 8 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Sophocarpin is generated from a SMILES and the ring that only has an overlapping bond is removed.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme8Rule4and5OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CC2CN3C(CC=CC3=O)C4C2N(C1)CCC4");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme8").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme8/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleFourAndFive(tmpSchuffenhauerScaffold, this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme8").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme8/RuleFourOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("N1CC2CCCN3CCCC(C1)C32", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 9 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Rhynchophylline is generated from a SMILES and the aromatic ring is removed.
     * The 6 ring is now removed from this fragment.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme9Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CCC1CN2CCC3(C2CC1C(=COC)C(=O)OC)C4=CC=CC=C4NC3=O");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpMod = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMod.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9").mkdirs();
        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9/Modified.png");
        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9/RuleFour.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1NC=CC12CNCC2", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads Scheme 9 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Rhynchophylline without the aromatic ring is generated from a SMILES.
     * The 6 ring is now removed from this fragment.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme9Rule4and5OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O=C1NC=CC12CCN3CCCCC32");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleFourAndFive(tmpSchuffenhauerScaffold, this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme9/RuleFourOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1NC=CC12CNCC2", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 10 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Cafestol is generated from a SMILES and the aromatic ring is removed.
     * The 6 ring is now removed from this fragment.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme10Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC12CCC3=C(C1CCC45C2CCC(C4)C(C5)(CO)O)C=CO3");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpMod = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMod.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10").mkdirs();
        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10/Modified.png");
        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(3)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10/RuleFive.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1CC2CCC(C1)C2", tmpSmilesGenerator.create(tmpRule.get(3)));
    }

    /**
     * Loads Scheme 10 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Cafestol without the rings that contains double bonds is generated from a SMILES.
     * The 6 ring is now removed from this fragment.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme10Rule4and5OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CCC23CCC(CCC2C1)C3");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleFourAndFive(tmpSchuffenhauerScaffold, this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme10/RuleFiveOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1CC2CCC(C1)C2", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 11a from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Flucloxacillin is generated from a SMILES and the rings connected via linkers are removed.
     * Then, according to the sixth rule, the ring of size 5 is removed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme11aTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        //tmpMolecule = tmpParser.parseSmiles("C1CCC23CCC(CCC2C1)C3");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpMod = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMod.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a").mkdirs();
        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a/Modified.png");
        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(3)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a/RuleSix.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1NCC1", tmpSmilesGenerator.create(tmpRule.get(3)));
    }

    /**
     * Loads Scheme 11a from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Flucloxacillin without rings connected via linkers is generated from a SMILES.
     * Then, according to the sixth rule, the ring of size 5 is removed.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme11aRule6OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O=C1N2CCSC2C1");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleSix(this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11a/RuleSixOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1NCC1", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 11b from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Epinastine is generated from a SMILES and the aromatic rings are removed.
     * Then, according to the sixth rule, the ring of size 5 is removed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme11bTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1C2C3=CC=CC=C3CC4=CC=CC=C4N2C(=N1)N");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpMod = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMod.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b").mkdirs();
        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b/Modified.png");
        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(3)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b/RuleSix.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1=CCC=CCN1", tmpSmilesGenerator.create(tmpRule.get(3)));
    }

    /**
     * Loads Scheme 11b from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Epinastine without aromatic rings is generated from a SMILES.
     * Then, according to the sixth rule, the ring of size 5 is removed.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme11bRule6OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("N1=CN2C=CCC=CC2C1");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleSix(this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme11b/RuleSixOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1=CCC=CCN1", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 12 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Zaleplon is generated from a SMILES and the C 6 ring is removed.
     * Then, according to the seventh rule, the ring of size 6 is removed.
     * If rule 7 is turned off the 5 ring is removed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme12Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CCN(C1=CC=CC(=C1)C2=CC=NC3=C(C=NN23)C#N)C(=O)C"); //Original
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpMod = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMod.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12").mkdirs();
        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12/Modified.png");
        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12/RuleSeven.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("N1=CC=CN1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads Scheme 12 from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES.
     * Zaleplon with the C6 ring removed is generated from a SMILES.
     * Then, according to the seventh rule, the ring of size 6 is removed.
     * If rule 7 is turned off the 5 ring is removed.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme12Rule7OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("N1=CC=CN2N=CC=C12");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleSeven(tmpSchuffenhauerScaffold, this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme12/RuleSevenOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("N1=CC=CN1", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Test that illustrates aromaticity detection for pyrimidine, pyrazole, and their combination in
     * pyrazolo[1,5-a]pyrimidine, which is used in Scheme 12 for illustrating rule 7. Different cycle finder algorithms
     * and electron donation models are combined and the results printed as SMILES strings with aromaticity encoded.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void pyrazoloPyrimidineElectronDonationAndCycleFinderTest() throws Exception {
        String tmpPyrimidineSMILES = "C1=CC=NC=N1";
        String tmpPyrazoleSMILES = "C1=CC=NN1";
        String tmpPyrazoloPyrimidineSMILES = "N1(C=CC=N2)C2=CC=N1";
        HashMap<String, String> tmpMoleculesMap = new HashMap<>(5, 1);
        tmpMoleculesMap.put(tmpPyrimidineSMILES, "pyrimidine");
        tmpMoleculesMap.put(tmpPyrazoleSMILES, "pyrazole");
        tmpMoleculesMap.put(tmpPyrazoloPyrimidineSMILES, "Pyrazolo[1,5-a]pyrimidine");
        SmilesParser tmpSmiPar = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
        HashMap<ElectronDonation, String> tmpElectronDonationModelsMap = new HashMap(6, 1);
        tmpElectronDonationModelsMap.put(ElectronDonation.cdk(), "CDK");
        tmpElectronDonationModelsMap.put(ElectronDonation.cdkAllowingExocyclic(), "CDK exocyclic");
        tmpElectronDonationModelsMap.put(ElectronDonation.daylight(), "Daylight");
        tmpElectronDonationModelsMap.put(ElectronDonation.piBonds(), "pi bonds");
        CycleFinder[] tmpCycleFinders = {Cycles.all(), Cycles.cdkAromaticSet(), Cycles.mcb(), Cycles.relevant()};
        for (String tmpSMILES : tmpMoleculesMap.keySet()) {
            System.out.println("\n" + tmpMoleculesMap.get(tmpSMILES));
            for (CycleFinder tmpCF : tmpCycleFinders) {
                System.out.println("\n\t" + tmpCF);
                for (ElectronDonation tmpEDmodel : tmpElectronDonationModelsMap.keySet()) {
                    Aromaticity tmpAromaticityModel = new Aromaticity(tmpEDmodel, tmpCF);
                    IAtomContainer tmpMolecule = tmpSmiPar.parseSmiles(tmpSMILES);
                    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMolecule);
                    tmpAromaticityModel.apply(tmpMolecule);
                    int tmpNumberOfRings = tmpCF.find(tmpMolecule).numberOfCycles();
                    System.out.println("\t\t" + tmpElectronDonationModelsMap.get(tmpEDmodel) + " " + tmpNumberOfRings + " " + tmpSmiGen.create(tmpMolecule));
                }
            }
        }
    }

    /**
     * Loads Scheme 13 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * A double ring system is generated from a SMILES.
     * According to the eighth rule the ring with the least heterocycle is removed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme13Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("c2ccc1[nH]ccc1c2");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme13").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme13/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme13").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme13/RuleEight.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C=1C=CNC1", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads Scheme 13 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * A double ring system is generated from a SMILES.
     * According to the eighth rule the ring with the least heterocycle is removed.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme13Rule8OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("c2ccc1[nH]ccc1c2");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme13").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme13/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleEight(this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme13").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme13/RuleEightOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C=1C=CNC1", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 14 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Ticlopidine is generated from a SMILES.
     * According to the ninth rule the ring with the S is removed first.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme14Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CN(CC2=C1SC=C2)CC3=CC=CC=C3Cl.Cl");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpMod = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMod.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14").mkdirs();
        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14/Modified.png");
        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14/RuleNine.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1=CCCNC1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads Scheme 14 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Ticlopidine without the aromatic ring is generated from a SMILES.
     * According to the ninth rule the ring with the S is removed first.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme14Rule9OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("S1C=CC2=C1CCNC2");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleNine(this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme14/RuleNineOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1=CCCNC1", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads a molecule to check the rule ten from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * A molecule with two 7 rings and one 8 ring is generated from a SMILES.
     * According to the tenth rule the 7 rings are removed first.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getRule10Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O=C3CC2C(CCCC1CCCCCCC12)C(=O)C(=O)C3=O");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpMod = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMod.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10").mkdirs();
        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10/Modified.png");
        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10/RuleTen.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1CCCCCCC1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads a molecule to check the rule ten from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * A molecule with one 7 ring and one 8 ring is generated from a SMILES.
     * According to the tenth rule the 7 ring is removed first.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getRule10Rule10OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CCCC2CCCCCC2CC1");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleTen(this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Rule10/RuleTenOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1CCCCCCC1", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 15 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Sertraline is generated from a SMILES and the linker bonded 6 ring is removed.
     * According to the eleventh rule the aromatic ring is removed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme15Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CNC1CCC(C2=CC=CC=C12)C3=CC(=C(C=C3)Cl)Cl");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpMod = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMod.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15").mkdirs();
        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15/Modified.png");
        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15/RuleEleven.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1=CCCCC1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads Scheme 15 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Sertraline without the linker bonded 6 ring is generated from a SMILES.
     * According to the eleventh rule the aromatic ring is removed.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme15Rule11OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C=1C=CC2=C(C1)CCCC2");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleEleven(this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme15/RuleElevenOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("C1=CCCCC1", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 16 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Deferasirox is generated from a SMILES.
     * According to the twelfth rule the aromatic ring bond to the N is removed.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme16Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1=CC=C(C(=C1)C2=NN(C(=N2)C3=CC=CC=C3O)C4=CC=C(C=C4)C(=O)O)O");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setRuleSevenAppliedSetting(true);
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme16").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme16/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme16").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme16/RuleTwelve.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("N=1NC(=NC1C=2C=CC=CC2)C=3C=CC=CC3", tmpSmilesGenerator.create(tmpRule.get(1)));
    }

    /**
     * Loads Scheme 16 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Deferasirox is generated from a SMILES.
     * According to the twelfth rule the aromatic ring bond to the N is removed.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme16Rule12OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1=CC=C(C(=C1)C2=NN(C(=N2)C3=CC=CC=C3O)C4=CC=C(C=C4)C(=O)O)O");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setRuleSevenAppliedSetting(true);
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme16").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme16/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        List<IAtomContainer> tmpRemovalRings = tmpScaffoldGenerator.applySchuffenhauerRuleTwelve(tmpSchuffenhauerScaffold, this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        assertEquals(1, tmpRemovalRings.size()); //Only one fragment should be created
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(tmpSchuffenhauerScaffold, true, tmpRemovalRings.get(0));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme16").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme16/RuleTwelveOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("N=1NC(=NC1C=2C=CC=CC2)C=3C=CC=CC3", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 17 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Ormeloxifene is generated from a SMILES and the linker bonded 6 rings are removed.
     * The generated scaffold "Thirteen" does not correspond to the illustration in the paper.
     * This is due to the fact that unique SMILES are generated for rule 13, although canonical SMILES are used in the paper.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme17Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1(C(C(C2=C(O1)C=C(C=C2)OC)C3=CC=C(C=C3)OCCN4CCCC4)C5=CC=CC=C5)C");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpMod = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMod.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17").mkdirs();
        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17/Modified.png");
        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17/Thirteen.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate picture of the last step*/
        List<IAtomContainer> tmpLast = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgLast = tmpGenerator.depict(tmpLast.get(3)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17").mkdirs();
        File tmpOutputLast = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17/LastStep.png");
        ImageIO.write(tmpImgLast, "png" ,tmpOutputLast);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O1C=2C=CC=CC2C(C=3C=CC=CC3)CC1", tmpSmilesGenerator.create(tmpRule.get(2)));
    }

    /**
     * Loads Scheme 17 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Ormeloxifene without the linker bonded 6 ring is generated from a SMILE.
     * The generated scaffold "Thirteen" does not correspond to the illustration in the paper.
     * This is due to the fact that unique SMILES are generated for rule 13, although canical SMILES are used in the paper.
     * In this case, only the one rule to be checked is applied.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme17Rule13OnlyTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O1C=2C=CC=CC2C(C=3C=CC=CC3)C(C=4C=CC=CC4)C1");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17/Modified.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerScaffold with removed ring*/
        IAtomContainer tmpRingRemoved = tmpScaffoldGenerator.applySchuffenhauerRuleThirteen(tmpSchuffenhauerScaffold, this.getRingsForSchuffenhauer(tmpSchuffenhauerScaffold));
        //Remove the linkers
        IAtomContainer tmpSchuffRingRemoved = this.getScaffold(tmpRingRemoved, true);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpSchuffRingRemoved).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme17/ThirteenOnly.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O1C=2C=CC=CC2C(C=3C=CC=CC3)CC1", tmpSmilesGenerator.create(tmpSchuffRingRemoved));
    }

    /**
     * Loads Scheme 18 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Diazepam, Bromazepam, Zolazepam and Clotiazepam are generated from SMILES.
     * The Schuffenhauer rules are then applied to these molecules.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme18Test() throws Exception {
        /*-----Diazepam-----*/
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMoleculeDiazepam = tmpParser.parseSmiles("CN1C(=O)CN=C(C2=C1C=CC(=C2)Cl)C3=CC=CC=C3");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffoldDiazepam =tmpScaffoldGenerator.getScaffold(tmpMoleculeDiazepam, true);
        BufferedImage tmpImgSMILESDiazepam = tmpGenerator.depict(tmpSchuffenhauerScaffoldDiazepam).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputSMILESDiazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/DiazepamOriginal.png");
        ImageIO.write(tmpImgSMILESDiazepam, "png" ,tmpOutputSMILESDiazepam);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpStep1MolDiazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffoldDiazepam);
        BufferedImage tmpImgStep1Diazepam = tmpGenerator.depict(tmpStep1MolDiazepam.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputStep1Diazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/DiazepamStep1.png");
        ImageIO.write(tmpImgStep1Diazepam, "png" ,tmpOutputStep1Diazepam);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpStep2MolDiazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffoldDiazepam);
        BufferedImage tmpImgStep2Diazepam = tmpGenerator.depict(tmpStep2MolDiazepam.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputStep2Diazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/DiazepamStep2.png");
        ImageIO.write(tmpImgStep2Diazepam, "png" ,tmpOutputStep2Diazepam);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1NC=2C=CC=CC2C=NC1", tmpSmilesGenerator.create(tmpStep2MolDiazepam.get(1)));
        assertEquals("O=C1NC=CC=NC1", tmpSmilesGenerator.create(tmpStep2MolDiazepam.get(2)));

        /*-----Bromazepam-----*/
        //SMILES to IAtomContainer
        IAtomContainer tmpMoleculeBromazepam = tmpParser.parseSmiles("C1C(=O)NC2=C(C=C(C=C2)Br)C(=N1)C3=CC=CC=N3");
        /*Generate picture of the SchuffenhauerScaffold*/
        //Generate SchuffenhauerScaffold
        IAtomContainer tmpSchuffenhauerScaffoldBromazepam =tmpScaffoldGenerator.getScaffold(tmpMoleculeBromazepam, true);
        BufferedImage tmpImgSMILESBromazepam = tmpGenerator.depict(tmpSchuffenhauerScaffoldBromazepam).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputSMILESBromazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/BromazepamOriginal.png");
        ImageIO.write(tmpImgSMILESBromazepam, "png" ,tmpOutputSMILESBromazepam);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpStep1MolBromazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffoldBromazepam);
        BufferedImage tmpImgStep1Bromazepam = tmpGenerator.depict(tmpStep1MolBromazepam.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputStep1Bromazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/BromazepamStep1.png");
        ImageIO.write(tmpImgStep1Bromazepam, "png" ,tmpOutputStep1Bromazepam);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpStep2MolBromazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffoldBromazepam);
        BufferedImage tmpImgStep2Bromazepam = tmpGenerator.depict(tmpStep2MolBromazepam.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputStep2Bromazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/BromazepamStep2.png");
        ImageIO.write(tmpImgStep2Bromazepam, "png" ,tmpOutputStep2Bromazepam);
        /*Generate and check SMILES*/
        assertEquals("O=C1NC=2C=CC=CC2C=NC1", tmpSmilesGenerator.create(tmpStep2MolBromazepam.get(1)));
        assertEquals("O=C1NC=CC=NC1", tmpSmilesGenerator.create(tmpStep2MolBromazepam.get(2)));

        /*-----Zolazepam-----*/
        //SMILES to IAtomContainer
        IAtomContainer tmpMoleculeZolazepam = tmpParser.parseSmiles("CC1=NN(C2=C1C(=NCC(=O)N2C)C3=CC=CC=C3F)C");
        /*Generate picture of the SchuffenhauerScaffold*/
        //Generate SchuffenhauerScaffold
        IAtomContainer tmpSchuffenhauerScaffoldZolazepam =tmpScaffoldGenerator.getScaffold(tmpMoleculeZolazepam, true);
        BufferedImage tmpImgSMILESZolazepam = tmpGenerator.depict(tmpSchuffenhauerScaffoldZolazepam).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputSMILESZolazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/ZolazepamOriginal.png");
        ImageIO.write(tmpImgSMILESZolazepam, "png" ,tmpOutputSMILESZolazepam);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpStep1MolZolazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffoldZolazepam);
        BufferedImage tmpImgStep1Zolazepam = tmpGenerator.depict(tmpStep1MolZolazepam.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputStep1Zolazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/ZolazepamStep1.png");
        ImageIO.write(tmpImgStep1Zolazepam, "png" ,tmpOutputStep1Zolazepam);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpStep2MolZolazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffoldZolazepam);
        BufferedImage tmpImgStep2Zolazepam = tmpGenerator.depict(tmpStep2MolZolazepam.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputStep2Zolazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/ZolazepamStep2.png");
        ImageIO.write(tmpImgStep2Zolazepam, "png" ,tmpOutputStep2Zolazepam);
        /*Generate and check SMILES*/
        assertEquals("O=C1NC=2NN=CC2C=NC1", tmpSmilesGenerator.create(tmpStep2MolZolazepam.get(1)));
        assertEquals("O=C1NC=CC=NC1", tmpSmilesGenerator.create(tmpStep2MolZolazepam.get(2)));

        /*-----Clotiazepam-----*/
        //SMILES to IAtomContainer
        IAtomContainer tmpMoleculeClotiazepam = tmpParser.parseSmiles("CCC1=CC2=C(S1)N(C(=O)CN=C2C3=CC=CC=C3Cl)C");
        /*Generate picture of the SchuffenhauerScaffold*/
        //Generate SchuffenhauerScaffold
        IAtomContainer tmpSchuffenhauerScaffoldClotiazepam =tmpScaffoldGenerator.getScaffold(tmpMoleculeClotiazepam, true);
        BufferedImage tmpImgSMILESClotiazepam = tmpGenerator.depict(tmpSchuffenhauerScaffoldClotiazepam).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputSMILESClotiazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/ClotiazepamOriginal.png");
        ImageIO.write(tmpImgSMILESClotiazepam, "png" ,tmpOutputSMILESClotiazepam);
        /*Generate picture of the modified molecule*/
        List<IAtomContainer> tmpStep1MolClotiazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffoldClotiazepam);
        BufferedImage tmpImgStep1Clotiazepam = tmpGenerator.depict(tmpStep1MolClotiazepam.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputStep1Clotiazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/ClotiazepamStep1.png");
        ImageIO.write(tmpImgStep1Clotiazepam, "png" ,tmpOutputStep1Clotiazepam);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpStep2MolClotiazepam = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffoldClotiazepam);
        BufferedImage tmpImgStep2Clotiazepam = tmpGenerator.depict(tmpStep2MolClotiazepam.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18").mkdirs();
        File tmpOutputStep2Clotiazepam = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme18/ClotiazepamStep2.png");
        ImageIO.write(tmpImgStep2Clotiazepam, "png" ,tmpOutputStep2Clotiazepam);
        /*Generate and check SMILES*/
        assertEquals("O=C1NC=2SC=CC2C=NC1", tmpSmilesGenerator.create(tmpStep2MolClotiazepam.get(1)));
        assertEquals("O=C1NC=CC=NC1", tmpSmilesGenerator.create(tmpStep2MolClotiazepam.get(2)));
    }

    /**
     * Loads Scheme 19 from the "The Scaffold Tree" Paper by Schuffenhauer et al as SMILES.
     * Baccatin III is generated from a SMILES and decomposed according to the Schuffenahauer rules.
     * -Step 1: The aromatic 6 ring is removed according to rule 3
     * -Step 2: The 4 ring is removed according to rule 4
     * -Step 3: The 6 ring without DB is removed according to rule 4
     * -Step 4: The 6 ring with DB is removed according to rule 6
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getScheme19Test() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C2C(C(=O)C3(C(CC4C(C3C(C(C2(C)C)(CC1O)O)OC(=O)C5=CC=CC=C5)(CO4)OC(=O)C)O)C)OC(=O)C");
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate the scaffolds*/
        List<IAtomContainer> tmpScaffolds = tmpScaffoldGenerator.applySchuffenhauerRules(tmpSchuffenhauerScaffold);
        /*Generate picture of the modified molecule*/
        BufferedImage tmpImgStep1 = tmpGenerator.depict(tmpScaffolds.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19").mkdirs();
        File tmpOutputStep1 = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19/Step1.png");
        ImageIO.write(tmpImgStep1, "png" ,tmpOutputStep1);
        /*Generate picture of the modified molecule*/
        BufferedImage tmpImgStep2 = tmpGenerator.depict(tmpScaffolds.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19").mkdirs();
        File tmpOutputStep2 = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19/Step2.png");
        ImageIO.write(tmpImgStep2, "png" ,tmpOutputStep2);
        /*Generate picture of the modified molecule*/
        BufferedImage tmpImgStep3 = tmpGenerator.depict(tmpScaffolds.get(3)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19").mkdirs();
        File tmpOutputStep3 = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19/Step3.png");
        ImageIO.write(tmpImgStep3, "png" ,tmpOutputStep3);
        /*Generate picture of the modified molecule*/
        BufferedImage tmpImgStep4 = tmpGenerator.depict(tmpScaffolds.get(4)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19").mkdirs();
        File tmpOutputStep4 = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SchuffenhauerRules/Scheme19/Step4.png");
        ImageIO.write(tmpImgStep4, "png" ,tmpOutputStep4);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C1CC2=CCCC(C2)CC3C1CCC4OCC43", tmpSmilesGenerator.create(tmpScaffolds.get(1)));
        assertEquals("O=C1CC2=CCCC(C2)CC3CCCCC13", tmpSmilesGenerator.create(tmpScaffolds.get(2)));
        assertEquals("O=C1CC2=CCCC(C2)CCC1", tmpSmilesGenerator.create(tmpScaffolds.get(3)));
        assertEquals("O=C1CCCCCCC1", tmpSmilesGenerator.create(tmpScaffolds.get(4)));
    }
    //</editor-fold>

    //<editor-fold desc="Settings">
    /**
     * Test of ScaffoldGenerator.setRuleSevenAppliedSetting() with SMILES of Scheme 12.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void setRuleSevenAppliedSettingTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CCN(C1=CC=CC(=C1)C2=CC=NC3=C(C=NN23)C#N)C(=O)C"); //Scheme12
        /*Generate picture of the SchuffenhauerScaffold*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        IAtomContainer tmpSchuffenhauerScaffold = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerScaffold).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/isRule7Applied").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/isRule7Applied/Original.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate picture of the SchuffenhauerRule*/
        List<IAtomContainer> tmpRule = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRule = tmpGenerator.depict(tmpRule.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/isRule7Applied").mkdirs();
        File tmpOutputRule = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/isRule7Applied/RuleSevenTrue.png");
        ImageIO.write(tmpImgRule, "png" ,tmpOutputRule);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("N1=CC=CN1", tmpSmilesGenerator.create(tmpRule.get(2)));
        /*Generate picture of the SchuffenhauerRule without Rule 7*/
        tmpScaffoldGenerator.setRuleSevenAppliedSetting(false);
        List<IAtomContainer> tmpRuleFalse = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgRuleFalse = tmpGenerator.depict(tmpRuleFalse.get(2)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/isRule7Applied").mkdirs();
        File tmpOutputRuleFalse = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/isRule7Applied/RuleSevenFalse.png");
        ImageIO.write(tmpImgRuleFalse, "png" ,tmpOutputRuleFalse);
        /*Generate and check SMILES*/
        assertEquals("N1=CC=CNC1", tmpSmilesGenerator.create(tmpRuleFalse.get(2)));
    }

    /**
     * Test of ScaffoldGenerator.setScaffoldModeSetting() with SMILES.
     * Loads Scheme 1 (Flucloxacillin) from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES and
     * generates the Schuffenhauer Scaffold, the Murcko Scaffold and the Basic Wire Frame.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void setScaffoldModeSettingTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        /*Generate picture of the Original molecule*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        BufferedImage tmpImgOriginal = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture of the original*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputOriginal = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/Original.png");
        ImageIO.write(tmpImgOriginal, "png" ,tmpOutputOriginal);
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.SCHUFFENHAUER_SCAFFOLD);
        IAtomContainer tmpSchuffenhauerSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/Schuffenhauer.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C(NC1C(=O)N2CCSC21)C3=CON=C3C=4C=CC=CC4", tmpSmilesGenerator.create(tmpSchuffenhauerSMILES));
        /*Generate Murcko Scaffold*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.MURCKO_FRAMEWORK);
        IAtomContainer tmpMurckoSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgMurcko = tmpGenerator.depict(tmpMurckoSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputMurcko = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/Murcko.png");
        ImageIO.write(tmpImgMurcko, "png" ,tmpOutputMurcko);
        /*Generate and check SMILES*/
        assertEquals("N=1OC=C(C1C=2C=CC=CC2)CNC3CN4CCSC43", tmpSmilesGenerator.create(tmpMurckoSMILES));
        /*Generate Basic Wire Frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.BECCARI_BASIC_WIRE_FRAME);
        IAtomContainer tmpBWFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgBWF = tmpGenerator.depict(tmpBWFSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputBWF = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/BasicWireFrame.png");
        ImageIO.write(tmpImgBWF, "png" ,tmpOutputBWF);
        /*Generate and check SMILES*/
        assertEquals("C1CCC(CC1)C2CCCC2CCC3CC4CCCC43", tmpSmilesGenerator.create(tmpBWFSMILES));
        /*Generate Element Wire Frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
        IAtomContainer tmpEWFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgEWF = tmpGenerator.depict(tmpEWFSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputEWF = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/ElementWireFrame.png");
        ImageIO.write(tmpImgEWF, "png" ,tmpOutputEWF);
        /*Generate and check SMILES*/
        assertEquals("O1NC(C(C1)CNC2CN3CCSC32)C4CCCCC4", tmpSmilesGenerator.create(tmpEWFSMILES));
        /*Generate Basic Framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.BECCARI_BASIC_FRAMEWORK);
        IAtomContainer tmpBFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgBF = tmpGenerator.depict(tmpBFSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputBF = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/BasicFramework.png");
        ImageIO.write(tmpImgBF, "png" ,tmpOutputBF);
        /*Generate and check SMILES*/
        System.out.println(tmpSmilesGenerator.create(tmpBFSMILES));
        assertEquals("C=1C=CC(=CC1)C2=CCC=C2CCC3CC4CCCC34", tmpSmilesGenerator.create(tmpBFSMILES));
    }

    /**
     * Test of ScaffoldGenerator.setScaffoldModeSetting() with SMILES.
     * Loads Scheme 1 (Flucloxacillin) from the "The Scaffold Tree" Paper by Schuffenhauer et al. as SMILES and
     * generates the Schuffenhauer Scaffold, the Murcko Scaffold and the Basic Wire Frame.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void setScaffoldModeSettingWithoutHTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        /*Generate picture of the Original molecule*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        BufferedImage tmpImgOriginal = tmpGenerator.depict(tmpMolecule).toImg();
        /*Save the picture of the original*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputOriginal = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/Original.png");
        ImageIO.write(tmpImgOriginal, "png" ,tmpOutputOriginal);
        //Generate SchuffenhauerScaffold
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.SCHUFFENHAUER_SCAFFOLD);
        IAtomContainer tmpSchuffenhauerSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgSMILES = tmpGenerator.depict(tmpSchuffenhauerSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputSMILES = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/SchuffenhauerWithoutH.png");
        ImageIO.write(tmpImgSMILES, "png" ,tmpOutputSMILES);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("O=C(NC1C(=O)N2[CH][C]SC21)C3=[C]ON=C3C=4[C]=CC=C[C]4", tmpSmilesGenerator.create(tmpSchuffenhauerSMILES));
        /*Generate Murcko Scaffold*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.MURCKO_FRAMEWORK);
        IAtomContainer tmpMurckoSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgMurcko = tmpGenerator.depict(tmpMurckoSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputMurcko = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/MurckoWithoutH.png");
        ImageIO.write(tmpImgMurcko, "png" ,tmpOutputMurcko);
        /*Generate and check SMILES*/
        assertEquals("[C]1SC2N([C]C2N[C]C3=[C]ON=C3C=4[C]=CC=C[C]4)[CH]1", tmpSmilesGenerator.create(tmpMurckoSMILES));
        /*Generate Basic Wire Frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.BECCARI_BASIC_WIRE_FRAME);
        IAtomContainer tmpBWFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgBWF = tmpGenerator.depict(tmpBWFSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputBWF = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/BasicWireFrameWithoutH.png");
        ImageIO.write(tmpImgBWF, "png" ,tmpOutputBWF);
        /*Generate and check SMILES*/
        assertEquals("[C]1[C][C][C]([C][C]1)[C]2[C][C][C][C]2[C][C][C]3[C][C]4[C][C][C][C]43", tmpSmilesGenerator.create(tmpBWFSMILES));
        /*Generate Element Wire Frame*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.ELEMENTAL_WIRE_FRAME);
        IAtomContainer tmpEWFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgEWF = tmpGenerator.depict(tmpEWFSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputEWF = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/ElementWireFrameWithoutH.png");
        ImageIO.write(tmpImgEWF, "png" ,tmpOutputEWF);
        /*Generate and check SMILES*/
        assertEquals("[C]1[C][C][C]([C][C]1)[C]2[N]O[C][C]2[C][N][C]3[C]N4[C][C]S[C]34", tmpSmilesGenerator.create(tmpEWFSMILES));
        /*Generate Basic Framework*/
        tmpScaffoldGenerator.setScaffoldModeSetting(ScaffoldGenerator.ScaffoldModeOption.BECCARI_BASIC_FRAMEWORK);
        IAtomContainer tmpBFSMILES = tmpScaffoldGenerator.getScaffold(tmpMolecule, false);
        /*Generate picture of the SchuffenhauerScaffold*/
        BufferedImage tmpImgBF = tmpGenerator.depict(tmpBFSMILES).toImg();
        /*Save the picture of the schuffenhauer scaffold*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest").mkdirs();
        File tmpOutputBF = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/ScaffoldModeTest/BasicFrameworkWithoutH.png");
        ImageIO.write(tmpImgBF, "png" ,tmpOutputBF);
        /*Generate and check SMILES*/
        System.out.println(tmpSmilesGenerator.create(tmpBFSMILES));
        assertEquals("[C]1[C]=C([C][CH]C2[C][C]3[CH][C][C]C32)C(=[C]1)C4=[C]C=CC=[C]4", tmpSmilesGenerator.create(tmpBFSMILES));
    }

    /**
     * Test of ScaffoldGenerator.setNonAromaticDBObtainedSetting() with SMILES.
     * All generated scaffolds are saved as images in a subfolder of the scaffoldTestOutput folder.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void setRetainOnlyHybridisationsAtAromaticBondsSettingTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C2=C1CCNCC1=CCC2");
        /*Generate picture of molecule*/
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(true);
        List<IAtomContainer> tmpSchuffenhauerFragments = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        /*Generate picture of the Original*/
        BufferedImage tmpImgFragment = tmpGenerator.depict(tmpSchuffenhauerFragments.get(0)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/RetainOnlyHybridisationsAtAromaticBondsSetting").mkdirs();
        File tmpOutputFragment = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/RetainOnlyHybridisationsAtAromaticBondsSetting/Original.png");
        ImageIO.write(tmpImgFragment, "png", tmpOutputFragment);
        /*Generate picture with NonAromaticDBObtainedSetting turned off*/
        BufferedImage tmpImgFragmentFalse = tmpGenerator.depict(tmpSchuffenhauerFragments.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/RetainOnlyHybridisationsAtAromaticBondsSetting").mkdirs();
        File tmpOutputFragmentFalse = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/RetainOnlyHybridisationsAtAromaticBondsSetting/DoNotKeepNonAromaticDB.png");
        ImageIO.write(tmpImgFragmentFalse, "png", tmpOutputFragmentFalse);
        /*Generate and check SMILES*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        assertEquals("N1CCCCC1", tmpSmilesGenerator.create(tmpSchuffenhauerFragments.get(1)));
        /*Generate picture with NonAromaticDBObtainedSetting turned on*/
        tmpScaffoldGenerator.setRetainOnlyHybridisationsAtAromaticBondsSetting(false);
        tmpSchuffenhauerFragments = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
        BufferedImage tmpImgFragmentTrue = tmpGenerator.depict(tmpSchuffenhauerFragments.get(1)).toImg();
        /*Save the picture*/
        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/RetainOnlyHybridisationsAtAromaticBondsSetting").mkdirs();
        File tmpOutputFragmentTrue = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Settings/RetainOnlyHybridisationsAtAromaticBondsSetting/KeepNonAromaticDB.png");
        ImageIO.write(tmpImgFragmentTrue, "png", tmpOutputFragmentTrue);
        /*Generate and check SMILES*/
        assertEquals("C1=CCCNC1", tmpSmilesGenerator.create(tmpSchuffenhauerFragments.get(1)));
    }
    //</editor-fold>

    //<editor-fold desc="Speed tests">
    /**
     * Speed test for the getScaffold() Method with over 400000 molecules from the COCONUT DB.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void calculateSchuffenhauerSpeedTest() throws Exception {
        this.ScaffoldGeneratorSpeedTest(true, false, false, false, false, true, 4242);
    }

    /**
     * Speed test for the getRings() Method with over 400000 molecules from the COCONUT DB.
     * getScaffold() must also be executed for all molecules.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void calculateRingsSpeedTest() throws Exception {
        this.ScaffoldGeneratorSpeedTest(false, true, false, false, false, false, 4242);
    }

    /**
     * Speed test for the removeRing() Method with over 400000 molecules from the COCONUT DB.
     * getScaffold() and getRings() must also be executed for all molecules.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void calculateRemoveRingsSpeedTest() throws Exception {
        this.ScaffoldGeneratorSpeedTest(false, false, true, false, false, true, 4242);
    }

    /**
     * Speed test for the removeRing() Method with over 400000 molecules from the COCONUT DB.
     * getScaffold() and getRings() must also be executed for all molecules.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void calculateApplySchuffenhauerRulesSpeedTest() throws Exception {
        this.ScaffoldGeneratorSpeedTest(false, false, false, true, false, true, 4242);
    }

    /**
     * Speed test for the getRemovalNetworkRules Method with over 400000 molecules from the COCONUT DB.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void calculateGetRemovalNetworkSpeedTest() throws Exception {
        this.ScaffoldGeneratorSpeedTest(false, false, false, true, false, true, 4242);
    }

    /**
     * Speed test for the getScaffold(), getRing() and removeRing() Method with over 400000 molecules from the COCONUT DB.
     * Which methods are tested can be set via the booleans.
     * To perform the test download the COCONUT DB(https://coconut.naturalproducts.net/download) and add the COCONUT_DB.sdf file to src\test\resources
     * @param anIsSchuffenhauerScaffoldCalculated Generate SchuffenhauerScaffolds
     * @param anIsRingCalculated Calculate Rings and Schuffenhauer scaffolds.
     * @param anIsRemoveRingCalculated The molecules for which the rings have been removed from the Schuffenhauer scaffolds are calculated. The Schuffenhauer scaffolds and the Rings are also calculated for this.
     * @param anIsApplySchuffenhauerCalculated Creates all molecule fragments generated by the schuffenhauer rules
     * @param anIsPictureCreated Show control pictures from one molecule.
     * @param aPictureNumber Number of the molecule from which control images are to be taken(from 0 to 406000)
     * @throws Exception if anything goes wrong
     */
    private void ScaffoldGeneratorSpeedTest(boolean anIsSchuffenhauerScaffoldCalculated, boolean anIsRingCalculated, boolean anIsRemoveRingCalculated,
                                            boolean anIsApplySchuffenhauerCalculated, boolean anIsGetRemovalNetworkCalculated, boolean anIsPictureCreated, int aPictureNumber) throws Exception {
        /*Counter*/
        int tmpExceptionCounter = 0;
        int tmpNumberCounter = 0;
        int tmpSkipCounter = 0;
        int tmpCounter = 0;
        //Number of molecules that have more than 10 rings and were skipped in the applyEnumerativeRemoval speed test
        //Number of molecules where more than 1000 rings were detected and skipped in the calculateRemoveRingsSpeedTest().
        int tmpFusedRingCounter = 0;
        /*Loading and reading the library*/
        File tmpResourcesDirectory = new File("src/test/resources/COCONUT_DB.sdf");
        IteratingSDFReader tmpReader = new IteratingSDFReader( new FileInputStream(tmpResourcesDirectory), DefaultChemObjectBuilder.getInstance());
        //Start timer
        long tmpStartTime = System.nanoTime();
        /*Start report*/
        System.out.println("-----START REPORT-----");
        if(anIsSchuffenhauerScaffoldCalculated == true && anIsRingCalculated == false) {
            System.out.println("In this test, the Schuffenhauer scaffolds are calculated for all molecules.");
        }
        if(anIsRingCalculated == true && anIsRemoveRingCalculated == false) {
            System.out.println("In this test, the Schuffenhauer scaffolds and their rings are calculated for all molecules.");
        }
        if(anIsRemoveRingCalculated == true && anIsApplySchuffenhauerCalculated == false){
            System.out.println("In this test, the Schuffenhauer scaffolds and their rings are calculated for all molecules.");
            System.out.println("In addition, the molecules for which the rings have been removed from the Schuffenhauer scaffolds are calculated.");
        }
        if(anIsApplySchuffenhauerCalculated == true){
            System.out.println("In this test, the molecules are decomposed according to the Schuffenhauer rules");
        }
        /*Going through the library*/
        while (tmpReader.hasNext()) {
            String tmpCoconutID = null;
            IAtomContainer tmpMolecule = (IAtomContainer) tmpReader.next();
            tmpCoconutID = tmpMolecule.getProperty("coconut_id");
            try {
                /*Calculate SchuffenhauerScaffolds*/
                if(anIsSchuffenhauerScaffoldCalculated == true && anIsRingCalculated == false) {
                    ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
                    tmpMolecule = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
                    /*Generate control picture*/
                    if(anIsPictureCreated && (aPictureNumber) == tmpNumberCounter) {
                        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                        /*Generate and save molecule picture*/
                        BufferedImage tmpImgMol = tmpGenerator.depict(tmpMolecule).toImg();
                        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest").mkdirs();
                        File tmpOutputMol = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest/SpeedTestMol.png");
                        ImageIO.write(tmpImgMol, "png", tmpOutputMol);
                    }
                }
                /*Calculate SchuffenhauerScaffolds and Rings*/
                if(anIsRingCalculated == true && anIsRemoveRingCalculated == false) {
                    ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
                    tmpMolecule = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
                    List<IAtomContainer> tmpRings = tmpScaffoldGenerator.getRingsInternal(tmpMolecule, true);
                    /*Generate control pictures*/
                    if(anIsPictureCreated && (aPictureNumber) == tmpNumberCounter) {
                        IAtomContainer tmpRing = tmpRings.get(tmpRings.size()-1);
                        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                        /*Generate and save molecule picture*/
                        BufferedImage tmpImgMol = tmpGenerator.depict(tmpMolecule).toImg();
                        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest").mkdirs();
                        File tmpOutputMol = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest/SpeedTestMol.png");
                        ImageIO.write(tmpImgMol, "png", tmpOutputMol);
                        /*Generate and save ring picture*/
                        BufferedImage tmpImgRing = tmpGenerator.depict(tmpRing).toImg();
                        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest").mkdirs();
                        File tmpOutputRing = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest/SpeedTestRing.png");
                        ImageIO.write(tmpImgRing, "png", tmpOutputRing);
                    }
                }
                /*Calculate SchuffenhauerScaffolds, Rings and the molecules for which the rings have been removed from the Schuffenhauer scaffolds*/
                if(anIsRemoveRingCalculated == true && anIsApplySchuffenhauerCalculated == false) {
                    ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
                    IAtomContainer tmpSchuff = tmpScaffoldGenerator.getScaffold(tmpMolecule, true);
                    List<IAtomContainer> tmpRings =tmpScaffoldGenerator.getRingsInternal(tmpSchuff,true);
                    /*Skip all molecules with more than 1000 rings*/
                    if(tmpRings.size() > 100) {
                        System.out.println(tmpCoconutID);
                        System.out.println("Skipped Ring size:  " + tmpRings.size());
                        tmpNumberCounter++;
                        tmpSkipCounter++;
                        continue;
                    }
                    boolean tmpIsFused = false;
                    List<IAtomContainer> tmpRemovableRings = new ArrayList<>(tmpRings.size());
                    for(IAtomContainer tmpRing : tmpRings) {
                        if(tmpScaffoldGenerator.isRingRemovable(tmpRing, tmpRings, tmpSchuff) &&tmpScaffoldGenerator.isRingTerminal(tmpSchuff, tmpRing)) {
                            tmpRemovableRings.add(tmpRing);
                        }
                        /*Detect molecules with aromatic fused ring systems*/
                        if(tmpScaffoldGenerator.hasFusedAromaticRings(tmpRing,tmpRings,tmpSchuff) == true) {
                            tmpIsFused = true;
                        }
                        IAtomContainer tmpRemoveMol =tmpScaffoldGenerator.removeRing(tmpSchuff, true, tmpRing);
                        /*Generate control pictures*/
                        if(anIsPictureCreated && (aPictureNumber) == tmpNumberCounter) {
                            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                            /*Generate and save molecule picture*/
                            BufferedImage tmpImgMol = tmpGenerator.depict(tmpSchuff).toImg();
                            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest").mkdirs();
                            File tmpOutputMol = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest/SpeedTestMol.png");
                            ImageIO.write(tmpImgMol, "png", tmpOutputMol);
                            /*Generate and save ring picture*/
                            BufferedImage tmpImgRing = tmpGenerator.depict(tmpRing).toImg();
                            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest").mkdirs();
                            File tmpOutputRing = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest/SpeedTestRing.png");
                            ImageIO.write(tmpImgRing, "png", tmpOutputRing);
                            /*Generate and save removed ring picture*/
                            BufferedImage tmpImgRemove = tmpGenerator.depict(tmpRemoveMol).toImg();
                            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest").mkdirs();
                            File tmpOutputRemove = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest/SpeedTestRingRemoved.png");
                            ImageIO.write(tmpImgRemove, "png", tmpOutputRemove);
                        }
                    }
                    /*Detected molecule with aromatic fused ring system*/
                    if(tmpIsFused) {
                        tmpMolecule = AtomContainerManipulator.removeHydrogens(tmpMolecule);
                        //CDKHydrogenAdder.getInstance(tmpMolecule.getBuilder()).addImplicitHydrogens(tmpMolecule);
                        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMolecule).toImg();
                        /*Save the picture*/
                        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/AromaticFused").mkdirs();
                        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/AromaticFused/" + tmpCoconutID + ".png");
                        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);

                        System.out.println("Fused ring detected " + tmpCoconutID);
                        tmpFusedRingCounter++;
                    }
                }

                /*Calculate a list of molecules with iteratively removed terminal rings*/
                if(anIsApplySchuffenhauerCalculated == true && anIsGetRemovalNetworkCalculated == false) {
                    /*Skip molecules with to many rings if needed*/
                    ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
                    if(tmpScaffoldGenerator.getRingsInternal(tmpScaffoldGenerator.getScaffold(tmpMolecule, true),false).size() > 100) {
                        tmpSkipCounter++;
                        System.out.println("Molecule skipped: " + tmpCoconutID);
                        //continue;
                    }
                    /*Detect and save conspicuous molecules*/
                    boolean tmpIsConspicuous = false;
                    /*
                    IAtomContainer tmpSchuff =tmpScaffoldGenerator.getScaffold(tmpMolecule, false, null);
                    int tmpRingNumber = tmpScaffoldGenerator.getRings(tmpSchuff, false).size();
                    float tmpRingAtomRatio = (float) tmpSchuff.getAtomCount() / tmpRingNumber;
                    if(tmpRingAtomRatio < 1.0 ) {
                        tmpIsConspicuous = true;
                    }*/
                    List<IAtomContainer> tmpIterations = null;
                    try {
                        tmpIterations = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
                    } catch(Exception ex) {
                        System.out.println("----------Exception: all does not work" + tmpCoconutID);
                        tmpIterations = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
                    }
                    if(anIsPictureCreated && (aPictureNumber) == tmpNumberCounter) {
                        int tmpIterationCounter = 0;
                        for(IAtomContainer tmpIteration : tmpIterations) {
                            /*Generate control pictures*/
                            DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                            /*Generate and save molecule picture*/
                            BufferedImage tmpImgMol = tmpGenerator.depict(tmpMolecule).toImg();
                            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest").mkdirs();
                            File tmpOutputMol = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest/SpeedTestMol.png");
                            ImageIO.write(tmpImgMol, "png", tmpOutputMol);
                            /*Generate picture of the Iteration*/
                            BufferedImage tmpImgIter = tmpGenerator.depict(tmpIteration).toImg();
                            new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest").mkdirs();
                            File tmpOutputIter = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/SpeedTest/SpeedTestIteration" + tmpIterationCounter + ".png");
                            ImageIO.write(tmpImgIter, "png", tmpOutputIter);
                            tmpIterationCounter++;
                        }
                    }
                    /*All molecules that are considered conspicuous are stored as an image*/
                    if(tmpIsConspicuous == true) {
                        tmpMolecule = AtomContainerManipulator.removeHydrogens(tmpMolecule);
                        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                        BufferedImage tmpImgMod = tmpGenerator.depict(tmpMolecule).toImg();
                        /*Save the picture*/
                        new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Conspicuous").mkdirs();
                        File tmpOutputMod = new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Conspicuous/" + tmpCoconutID + ".png");
                        ImageIO.write(tmpImgMod, "png" ,tmpOutputMod);
                        System.out.println("Conspicuous: " + tmpCoconutID);
                        tmpFusedRingCounter++;
                    }
                }
                if(anIsGetRemovalNetworkCalculated == true) {
                    /*Skip molecules with to many rings if needed*/
                    ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
                    if(tmpScaffoldGenerator.getRingsInternal(tmpScaffoldGenerator.getScaffold(tmpMolecule, true),false).size() > 10) {
                        tmpSkipCounter++;
                        System.out.println("Molecule skipped: " + tmpCoconutID);
                        continue;
                    }
                    ScaffoldNetwork tmpNetwork = tmpScaffoldGenerator.generateEnumerativeNetwork(tmpMolecule);
                    if(tmpCounter == 10000){
                        System.out.println("Root size: " + tmpNetwork.getRoots().size());
                        System.out.println("Origin" + tmpNetwork.getRoots().get(0).getOriginSmilesList().get(0));
                    }
                }

                if(tmpCounter == 10000){
                    tmpCounter = 0;
                    System.out.println("At molecule Number: " + tmpNumberCounter);
                }


                /*First status report*/
                if(tmpNumberCounter == 100000) {
                    System.out.println("-----STATUS REPORT(1/4)-----");
                    System.out.println("A quarter of all molecules completed");
                    System.out.println("Number of exceptions: " + tmpExceptionCounter);
                    System.out.println("Runtime: " + TimeUnit.NANOSECONDS.toSeconds((System.nanoTime() - tmpStartTime)) + " seconds");
                }
                /*Second status report*/
                if(tmpNumberCounter == 200000) {
                    System.out.println("-----STATUS REPORT(2/4)-----");
                    System.out.println("A half of all molecules completed");
                    System.out.println("Number of exceptions: " + tmpExceptionCounter);
                    System.out.println("Runtime: " + TimeUnit.NANOSECONDS.toSeconds((System.nanoTime() - tmpStartTime)) + " seconds");
                }
                /*Third status report*/
                if(tmpNumberCounter == 300000) {
                    System.out.println("-----STATUS REPORT(3/4)-----");
                    System.out.println("Two thirds of all molecules completed");
                    System.out.println("Number of exceptions: " + tmpExceptionCounter);
                    System.out.println("Runtime: " + TimeUnit.NANOSECONDS.toSeconds((System.nanoTime() - tmpStartTime)) + " seconds");
                }
            }
            /*Count exceptions*/
            catch(Exception e) {
                System.out.println("Exception at number: " + tmpNumberCounter);
                System.out.println("COCONUT ID: " + tmpCoconutID);
                SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
                System.out.println("SMILES: " + tmpSmilesGenerator.create(tmpMolecule));
                /*Print out the exception stack trace*/
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString();
                System.out.println(sStackTrace);
                tmpExceptionCounter++;
                /*Generate control pictures*/
                DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
                /*Generate and save molecule picture*/
                tmpMolecule = AtomContainerManipulator.removeHydrogens(tmpMolecule);
                BufferedImage tmpImgMol = tmpGenerator.depict(tmpMolecule).toImg();
                new File(System.getProperty("user.dir") + "/scaffoldTestOutput/Exception").mkdirs();
                File tmpOutputMol = new File(System.getProperty("user.dir") +  "/scaffoldTestOutput/Exception/" + tmpCoconutID + ".png");
                ImageIO.write(tmpImgMol, "png", tmpOutputMol);
            }
            tmpCounter++;
            tmpNumberCounter++;
        }
        /*End report*/
        System.out.println("-----END REPORT-----");
        System.out.println("All molecules completed");
        System.out.println("Total number of exceptions: " + tmpExceptionCounter);
        System.out.println("total Runtime: " + TimeUnit.NANOSECONDS.toSeconds((System.nanoTime() - tmpStartTime)) + " seconds");
        System.out.println("Number of skipped molecules" + tmpSkipCounter);
        System.out.println("Number of fused molecules" + tmpFusedRingCounter);
    }

    /**
     * Measures the time it takes to decompose the COCONUT with the entire stereochemistry (975.000 molecules) using Schuffenhauers rules.
     * Counts the number of molecules whose stereoelements are reduced by the scaffold formation.
     * @throws Exception if anything goes wrong
     */
    @Ignore
    @Test
    public void calculateStereoSpeedTest() throws Exception {
        //Start timer
        long tmpStartTime = System.nanoTime();
        File tmpResourcesDirectory = new File("src/test/resources/COCONUT_DB.sdf");
        BufferedReader tmpBufferedReader = new BufferedReader(new FileReader("src/test/resources/coconut_all_stereo.smi"));
        String tmpRow = null;
        int tmpCounter = 0;
        int tmpInternCounter = 0;
        int tmpStereoChangeCounter = 0;
        int tmpExceptionCounter = 0;
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        ScaffoldGenerator tmpScaffoldGenerator = this.getScaffoldGeneratorTestSettings();
        tmpScaffoldGenerator.setSmilesGeneratorSetting(new SmilesGenerator(SmiFlavor.Isomeric));
        while ((tmpRow = tmpBufferedReader.readLine()) != null) {
            tmpCounter++;
            tmpInternCounter++;
            if(tmpInternCounter == 25000){
                tmpInternCounter = 0;
                System.out.println("Molecule Number: " + tmpCounter);
                System.out.println("Number of Exception: " + tmpExceptionCounter);
                System.out.println("Stereo Counter: " + tmpStereoChangeCounter);
                System.out.println("Runtime: " + TimeUnit.NANOSECONDS.toSeconds((System.nanoTime() - tmpStartTime)) + " seconds");
            }
            String[] tmpSplitted = tmpRow.split("\t");
            try{
                SmilesParser tmpSmiPar = new SmilesParser(DefaultChemObjectBuilder.getInstance());
                IAtomContainer tmpMolecule = tmpSmiPar.parseSmiles(tmpSplitted[0]);
                int tmpOriginalElementCounter = 0;
                for(IStereoElement tmpElement : tmpMolecule.stereoElements()) {
                    tmpOriginalElementCounter++;
                }
                List<IAtomContainer> tmpIterations = tmpScaffoldGenerator.applySchuffenhauerRules(tmpMolecule);
                int tmpScaffoldElementCounter = 0;
                for(IStereoElement tmpElement : tmpIterations.get(0).stereoElements()) {
                    tmpScaffoldElementCounter++;
                }
                if(tmpOriginalElementCounter != tmpScaffoldElementCounter) {
                    System.out.println("Original " + tmpSmilesGenerator.create(tmpMolecule));
                    System.out.println("Scaffold " + tmpSmilesGenerator.create(tmpIterations.get(0)));
                    tmpStereoChangeCounter++;
                }
            }
            /*Count exceptions*/
            catch(Exception e) {
                System.out.println("Exception at number: " + tmpCounter);
                System.out.println("COCONUT ID: " + tmpSplitted[1]);
                System.out.println("SMILES: " + tmpSplitted[0]);
                /*Print out the exception stack trace*/
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString();
                System.out.println(sStackTrace);
                tmpExceptionCounter++;
            }
        }
        System.out.println("Fin");
        System.out.println("Number of exceptions" + tmpExceptionCounter);
        System.out.println("total Runtime: " + TimeUnit.NANOSECONDS.toSeconds((System.nanoTime() - tmpStartTime)) + " seconds");
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="Protected methods">
    /**
     * Loads a mol file of a specific path and ret-----urns it as IAtomContainer.
     * Supports V2000 and V3000 mol files.
     * @param aFilePath Path of the molecule to be loaded
     * @return IAtomContainer of the charged molecule
     * @throws Exception if anything goes wrong
     */
    protected IAtomContainer loadMolFile(String aFilePath) throws Exception {
        /*Get molecule path*/
        File tmpResourcesDirectory = new File(aFilePath);
        BufferedInputStream tmpInputStream = new BufferedInputStream(new FileInputStream(tmpResourcesDirectory));
        /*Get mol file version*/
        FormatFactory tmpFactory = new FormatFactory();
        IChemFormat tmpFormat = tmpFactory.guessFormat(tmpInputStream);
        IAtomContainer tmpMolecule = new AtomContainer();
        /*Load V2000 mol file*/
        if(tmpFormat.getReaderClassName().contains("V2000")) {
            MDLV2000Reader tmpReader = new MDLV2000Reader(tmpInputStream);
            IChemObjectBuilder tmpBuilder = DefaultChemObjectBuilder.getInstance();
            tmpMolecule = tmpReader.read(tmpBuilder.newAtomContainer());
            /*Load V3000 mol file*/
        } else if(tmpFormat.getReaderClassName().contains("V3000")) {
            MDLV3000Reader tmpReader = new MDLV3000Reader(tmpInputStream);
            IChemObjectBuilder tmpBuilder = DefaultChemObjectBuilder.getInstance();
            tmpMolecule = tmpReader.read(tmpBuilder.newAtomContainer());
        }
        return tmpMolecule;
    }

    /**
     * Returns a ScaffoldGenerator Object with test settings corresponding to the default settings
     * @return a ScaffoldGenerator object with test settings
     * @throws Exception if anything goes wrong
     */
    protected ScaffoldGenerator getScaffoldGeneratorTestSettings() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator((SmiFlavor.Unique));
        tmpScaffoldGenerator.setSmilesGeneratorSetting(tmpSmilesGenerator);
        return tmpScaffoldGenerator;
    }

    /**
     * Identifies all removable rings of a molecule and returns them. The individual Schuffenhauer rules can be applied directly to the returned rings.
     * Is taken out of the applySchuffenhauerRules method.
     * @param aMolecule molecule whose removable rings are to be found.
     * @return List of removable rings.
     * @throws Exception if anything goes wrong
     */
    protected List<IAtomContainer> getRingsForSchuffenhauer(IAtomContainer aMolecule) throws Exception {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        List<IAtomContainer> tmpRemovableRings = new ArrayList<>();
        /*All molecules with an atom-to-ring ratio of less than 1.0 are assigned the CYCLE_FINDER_BACKUP_PROPERTY = true property,
         since to many rings were probably detected. The fact that a molecule has more rings than atoms seems concerning. That is why this value was chosen.*/
        int tmpRingNumber = this.getRingsInternal(tmpClonedMolecule, false).size();
        float tmpRingAtomRatio = (float) tmpClonedMolecule.getAtomCount() / tmpRingNumber;
        if(tmpRingAtomRatio < 1.0 ) {
            /*Change the property of all atoms of the molecule*/
            for(IAtom tmpAtom : tmpClonedMolecule.atoms()) {
                tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, true);
            }
            /*Apply the new Cyclefinder to the molecules*/
            tmpRingNumber = this.getRingsInternal(tmpClonedMolecule, false).size();
            tmpClonedMolecule = this.getScaffold(tmpClonedMolecule, true);
        }
        //List of all generated fragments
        List<IAtomContainer> tmpScaffoldFragments = new ArrayList<>(tmpRingNumber);
        tmpScaffoldFragments.add(tmpClonedMolecule);
        /*Go through all the fragments generated and try to break them down further*/
        for(int tmpCounter = 0 ; tmpCounter < tmpScaffoldFragments.size(); tmpCounter++) {
            List<IAtomContainer> tmpRings = this.getRingsInternal(tmpScaffoldFragments.get(tmpCounter), true);
            /*If the fragment has only one ring or no ring, it does not need to be disassembled further*/
            if (tmpRings.size() == 1 || tmpRings.size() == 0) {
                break;
            }
            /*Only the removable terminal rings are further investigated*/
            for (IAtomContainer tmpRing : tmpRings) {
                if (this.isRingTerminal(tmpScaffoldFragments.get(tmpCounter), tmpRing)
                        && this.isRingRemovable(tmpRing, tmpRings, tmpScaffoldFragments.get(tmpCounter))) {
                    tmpRemovableRings.add(tmpRing); //Add the candidate rings
                }
            }
        }
        return tmpRemovableRings;
    }
    //</editor-fold>
}