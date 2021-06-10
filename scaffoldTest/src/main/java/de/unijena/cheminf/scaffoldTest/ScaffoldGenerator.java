/*
 *
 * MIT License
 *
 * Copyright (c) 2021 Julian Zander, Jonas Schaub,  Achim Zielesny
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package de.unijena.cheminf.scaffoldTest;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fragment.MurckoFragmenter;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.CycleFinder;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ScaffoldGenerator {
    /**
     * Property of the atoms according to which they are counted and identified
     */
    public static final String SCAFFOLD_ATOM_COUNTER_PROPERTY = "SCAFFOLD_ATOM_COUNTER_PROPERTY";

    /**
     * Generates the Schuffenhauer scaffold for the entered molecule and returns it. All stereochemistry information is deleted.
     * @param aMolecule molecule whose Schuffenhauer scaffold is produced.
     * @return Schuffenhauer scaffold of the inserted molecule. It can be an empty molecule if the original molecule does not contain a Schuffenhauer scaffold.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    public IAtomContainer getSchuffenhauerScaffold(IAtomContainer aMolecule) throws CDKException, CloneNotSupportedException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        /*Clear the stereo chemistry of the molecule*/
        List<IStereoElement> tmpStereo = new ArrayList<>();
        tmpClonedMolecule.setStereoElements(tmpStereo);
        /*Mark each atom with ascending number*/
        Integer tmpCounter = 0;
        for(IAtom tmpAtom : tmpClonedMolecule.atoms()) {
            tmpAtom.setProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY, tmpCounter);
            tmpCounter++;
        }
        /*Generate the murckoFragment*/
        MurckoFragmenter tmpMurckoFragmenter = new MurckoFragmenter(true,1);
        tmpMurckoFragmenter.setComputeRingFragments(false);
        IAtomContainer tmpMurckoFragment = tmpMurckoFragmenter.scaffold(tmpClonedMolecule);
        /*Store the number of each Atom of the murckoFragment*/
        HashSet<Integer> tmpMurckoAtomNumbers = new HashSet(tmpClonedMolecule.getAtomCount(), 1);
        for(IAtom tmpMurckoAtom : tmpMurckoFragment.atoms()) {
            tmpMurckoAtomNumbers.add(tmpMurckoAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
        }
        /*Store the number of each Atom that is double-bonded and the respective bond*/
        //HashMap cannot be larger than the total number of atoms. Key = C and Val = Bond
        HashMap<Integer, IBond> tmpAddAtomMap = new HashMap((tmpClonedMolecule.getAtomCount()/2), 1);
        for(IBond tmpBond: tmpClonedMolecule.bonds()) {
            if(tmpBond.getOrder() == IBond.Order.DOUBLE) {//Consider all double bonds
                //If both atoms of the double bond are in the Murcko fragment, they are taken over anyway
                if(tmpMurckoAtomNumbers.contains(tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))
                        && tmpMurckoAtomNumbers.contains(tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                    continue;
                }
                //C in the first position in the bond and the binding has not yet been added to the list
                if(tmpBond.getAtom(0).getSymbol().equals("C") && !tmpAddAtomMap.containsValue(tmpBond)) {
                    tmpAddAtomMap.put(tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY),tmpBond);
                }
                //C in the second position in the bond and the binding has not yet been added to the list
                if(tmpBond.getAtom(1).getSymbol().equals("C") && !tmpAddAtomMap.containsValue(tmpBond)) {
                    tmpAddAtomMap.put(tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY),tmpBond);
                }
            }
        }
        /*Add the missing atom and the respective bond*/
        for(IAtom tmpAtom : tmpMurckoFragment.atoms()) {
            //Every atom that occurs in the tmpAddAtomMap and in the SchuffenhauerScaffold
            if(tmpAddAtomMap.containsKey(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                IBond tmpNewBond = null;
                //C in first position of the bond
                if(tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(0).getSymbol() == "C") {
                    IAtom tmpClonedAtom = tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(1).clone();
                    tmpMurckoFragment.addAtom(tmpClonedAtom); //Add cloned Atom to the molecule
                    //Clone the bond from the original molecule
                    tmpNewBond = tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).clone();
                    tmpNewBond.setAtom(tmpAtom,0); //Add tmpMurckoFragment C to the bond
                    tmpNewBond.setAtom(tmpClonedAtom,1); //Add cloned Atom to the bond
                }
                //C in second position of the bond and C is not in first position of the bond to prevent C=C bindings from being recorded twice
                if(tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(1).getSymbol() == "C" &&
                        tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(0).getSymbol() != "C" ) {
                    IAtom tmpClonedAtom = tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(0).clone();
                    tmpMurckoFragment.addAtom(tmpClonedAtom); //Add cloned Atom to the molecule
                    //Clone the bond from the original molecule
                    tmpNewBond = tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).clone();
                    tmpNewBond.setAtom(tmpAtom,1); //Add tmpMurckoFragment C to the bond
                    tmpNewBond.setAtom(tmpClonedAtom,0); //Add cloned Atom to the bond
                }
                tmpMurckoFragment.addBond(tmpNewBond); //Add the new bond
            }
        }
        /*Add back hydrogens removed by the MurckoFragmenter*/
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMurckoFragment);
        CDKHydrogenAdder.getInstance(tmpMurckoFragment.getBuilder()).addImplicitHydrogens(tmpMurckoFragment);
        return tmpMurckoFragment;
    }

    /**
     * Generates the smallest set of smallest rings(SSSR) with Cycles.mcb() for the entered molecule, adds double bounded oxygens and returns it.
     * @param aMolecule molecule whose rings are produced.
     * @param aHasDoubleBondedAtoms if true, double bonded atoms are retained on the ring.
     * @return rings of the inserted molecule.
     */
    public List<IAtomContainer> getRings(IAtomContainer aMolecule, boolean aHasDoubleBondedAtoms) throws CloneNotSupportedException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        /*Generate cycles*/
        Cycles tmpNewCycles = Cycles.mcb(tmpClonedMolecule);
        IRingSet tmpRingSet = tmpNewCycles.toRingSet();
        List<IAtomContainer> tmpCycles = new ArrayList<>(tmpNewCycles.numberOfCycles());
        int tmpCycleNumber = tmpNewCycles.numberOfCycles();
        //HashMap cannot be larger than the total number of atoms. Key = C and Val = Bond
        HashMap<Integer, IBond> tmpAddAtomMap = new HashMap((tmpClonedMolecule.getAtomCount() / 2), 1);
        /*Store double bonded atoms*/
        if(aHasDoubleBondedAtoms == true) { //Only needed if double bonded atoms are retained
            /*Store the number of each Atom of the molecule*/
            HashSet<Integer> tmpAtomNumbers = new HashSet(tmpClonedMolecule.getAtomCount(), 1);
            /*Generate the murckoFragment*/
            MurckoFragmenter tmpMurckoFragmenter = new MurckoFragmenter(true,1);
            tmpMurckoFragmenter.setComputeRingFragments(false);
            IAtomContainer tmpMurckoFragment = tmpMurckoFragmenter.scaffold(tmpClonedMolecule);
            /*Save the numbers of the MurckoFragment atoms, because double bonds between them do not need to be restored*/
            for (IAtom tmpAtom : tmpMurckoFragment.atoms()) {
                tmpAtomNumbers.add(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
            }

            /*Store the number of each C that is double-bonded to an Atom and the respective bond*/
            for (IBond tmpBond : tmpClonedMolecule.bonds()) {
                if (tmpBond.getOrder() == IBond.Order.DOUBLE) {//Consider all double bonds
                    //If both atoms of the double bond are in the Murcko fragment, they are taken over anyway
                    if (tmpAtomNumbers.contains(tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))
                            && tmpAtomNumbers.contains(tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                        continue;
                    }
                    //C in the first position in the bond and the binding has not yet been added to the list
                    if (tmpBond.getAtom(0).getSymbol().equals("C") && !tmpAddAtomMap.containsValue(tmpBond)) {
                        tmpAddAtomMap.put(tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY), tmpBond);
                    }
                    //C in the second position in the bond and the binding has not yet been added to the list
                    if (tmpBond.getAtom(1).getSymbol().equals("C") && !tmpAddAtomMap.containsValue(tmpBond)) {
                        tmpAddAtomMap.put(tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY), tmpBond);
                    }
                }
            }
        }
        /*Add Cycles*/
        for(int tmpCount = 0; tmpCount < tmpCycleNumber; tmpCount++) { //Go through all generated rings
            IAtomContainer tmpCycle = tmpRingSet.getAtomContainer(tmpCount); //Store rings as AtomContainer
            if(aHasDoubleBondedAtoms == true) {
                /*Add the missing atom and the respective bond*/
                for (IAtom tmpAtom : tmpCycle.atoms()) {
                    //Every atom that occurs in the tmpAddAtomMap and in the SchuffenhauerScaffold
                    if (tmpAddAtomMap.containsKey(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                        IBond tmpNewBond = null;
                        //C in first position of the bond
                        if (tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(0).getSymbol() == "C") {
                            IAtom tmpClonedAtom = tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(1).clone();
                            tmpCycle.addAtom(tmpClonedAtom); //Add cloned Atom to the molecule
                            //Clone the bond from the original molecule
                            tmpNewBond = tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).clone();
                            tmpNewBond.setAtom(tmpAtom, 0); //Add tmpMurckoFragment C to the bond
                            tmpNewBond.setAtom(tmpClonedAtom, 1); //Add cloned Atom to the bond
                        }
                        //C in second position of the bond and C is not in first position of the bond to prevent C=C bindings from being recorded twice
                        if (tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(1).getSymbol() == "C" &&
                                tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(0).getSymbol() != "C") {
                            IAtom tmpClonedAtom = tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).getAtom(0).clone();
                            tmpCycle.addAtom(tmpClonedAtom); //Add cloned Atom to the molecule
                            //Clone the bond from the original molecule
                            tmpNewBond = tmpAddAtomMap.get(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)).clone();
                            tmpNewBond.setAtom(tmpAtom, 1); //Add tmpMurckoFragment C to the bond
                            tmpNewBond.setAtom(tmpClonedAtom, 0); //Add cloned Atom to the bond
                        }
                        tmpCycle.addBond(tmpNewBond); //Add the new bond
                    }
                }
            }
            tmpCycles.add(tmpCycle); //Add rings to list
        }
        return tmpCycles;
    }


    /**
     * Removes the given ring from the total molecule and returns it.
     * Important: Property (ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY) must be set for tmpMolecule/tmpRing and match.
     * @param aMolecule Molecule whose ring is to be removed.
     * @param aRing Ring to be removed.
     * @return Molecule whose ring has been removed.
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     */
    public IAtomContainer removeRing(IAtomContainer aMolecule, IAtomContainer aRing) throws CloneNotSupportedException, CDKException {
        /*Clone original molecules*/
        IAtomContainer tmpMoleculeClone = aMolecule.clone();
        IAtomContainer tmpRingClone = aRing.clone();
        HashSet<Integer> tmpIsNotRing = new HashSet(aMolecule.getAtomCount(), 1);
        HashSet<Integer> tmpDoNotRemove = new HashSet(aMolecule.getAtomCount(), 1);
        int tmpBoundNumber = 0;
        /*Store the number of each atom in the molecule*/
        for(IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
            tmpIsNotRing.add(tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
        }
        /*Remove all numbers of the ring that is to be removed*/
        for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
            tmpIsNotRing.remove(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
        }
        /*Get the number of bonds of the ring to other atoms and store the bond atoms of the ring*/
        for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
            for(IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
                //All atoms of the ring in the original molecule
                if(tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY) == tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)) {
                    for(IBond tmpBond : tmpMolAtom.bonds()){
                        //Bond between ring an non ring atom
                        if(tmpIsNotRing.contains(tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                            tmpBoundNumber++;
                            //Store ring atom
                            tmpDoNotRemove.add(tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
                        }
                        //Bond between ring an non ring atom
                        if(tmpIsNotRing.contains(tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                            tmpBoundNumber++;
                            //Store ring atom
                            tmpDoNotRemove.add(tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
                        }
                    }
                }
            }
        }
        if(tmpBoundNumber < 2) { //Remove all ring atoms, as there are less than two bonds to other atoms
            for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
                for (IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
                    //All atoms of the ring in the original molecule
                    if (tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY) == tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)) {
                        tmpMoleculeClone.removeAtom(tmpMolAtom); //Remove atoms. tmpMoleculeCone.remove() not possible
                    }
                }
            }
        }
        else { //Remove only the ring atoms that are not bound to the rest of the molecule
            for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
                for (IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
                    /*All atoms of the ring in the original molecule that are not bound to the rest of the molecule*/
                    if ((tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)
                            == tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)) && !tmpDoNotRemove.contains(tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                        tmpMoleculeClone.removeAtom(tmpMolAtom); //Remove atoms
                    }
                }
            }
            /*Set the hybridisation to sp3 for all C that have only single bonds*/
            /**for(IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
                if(tmpMolAtom.getSymbol() == "C" && tmpMolAtom.getHybridization() != IAtomType.Hybridization.SP3) { //All C that are not sp3 hybridised
                    boolean tmpIsSp3 = true;
                    for(IBond tmpBond : tmpMolAtom.bonds()) { //All bonds of the C
                        if(tmpBond.getOrder() != IBond.Order.SINGLE) { //If it contains a non single bond it cannot be sp3
                            tmpIsSp3 = false;
                        }
                    }
                    if(tmpIsSp3) { //If the C contains only single bonds, it must be sp3
                        //tmpMolAtom.setHybridization(IAtomType.Hybridization.SP3); //Set sp3
                    }
                }
            }*/
        }
        /*Clear hybridisation. The hybridisation must be reset later by percieveAtomTypesAndConfigureAtoms, as the hybridisation is not changed on its own when the atoms are removed.
        sp2 atoms whose double bonds have been removed must be declared as sp3.*/
        for(IAtom tmpAtom : tmpMoleculeClone.atoms()) {
            tmpAtom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
        }
        /*Add back hydrogens removed by the MurckoFragmenter*/
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMoleculeClone);
        CDKHydrogenAdder.getInstance(tmpMoleculeClone.getBuilder()).addImplicitHydrogens(tmpMoleculeClone);
        return tmpMoleculeClone;
    }

    /**
     * Checks whether the tmpRing in the tmpMolecule is terminal. This means whether it can be removed without creating several unconnected parts.
     * Important: Property (ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY) must be set for tmpMolecule/tmpRing and match.
     * @param aMolecule Molecule whose ring is to be checked
     * @param aRing Ring to check
     * @return true if the tmpRing is terminal
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    public boolean isRingTerminal(IAtomContainer aMolecule, IAtomContainer aRing) throws CloneNotSupportedException {
        /*Clone molecule and ring*/
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        IAtomContainer tmpClonedRing = aRing.clone();
        /*Remove ring atoms from original molecule*/
        HashMap<Integer, IAtom> tmpMoleculeCounterMap = new HashMap((aMolecule.getAtomCount()), 1);
        for(IAtom tmpMolAtom : tmpClonedMolecule.atoms()) { //Save all atoms of the molecule
            tmpMoleculeCounterMap.put(tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY), tmpMolAtom);
        }
        for(IAtom tmpRingAtom : tmpClonedRing.atoms()) { // Go through the ring
            if(tmpMoleculeCounterMap.containsKey(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) { //Is ring atom in molecule
                tmpClonedMolecule.removeAtom(tmpMoleculeCounterMap.get(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))); //Remove them
            }
        }
        /*Check if there is more than one molecule in the IAtomContainer*/
        ConnectivityChecker tmpChecker = new ConnectivityChecker();
        boolean tmpRingIsTerminal = tmpChecker.isConnected(tmpClonedMolecule);
        return tmpRingIsTerminal;
    }

    public boolean isRingRemovable(IAtomContainer aMolecule, IAtomContainer aRing) throws CloneNotSupportedException{
        /*Clone molecule and ring*/
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        IAtomContainer tmpClonedRing = aRing.clone();

        //this.getRings()

        return true;
    }

    /**
     * Iteratively removes the terminal rings. All resulting Schuffenhauer scaffolds are returned. Duplicates are not permitted.
     * The Schuffenhauer scaffold of the entire entered molecule is stored first in the list.
     * @param aMolecule Molecule to be disassembled.
     * @return List with all resulting Schuffenhauer scaffolds.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    public List<IAtomContainer> getIterativeRemoval(IAtomContainer aMolecule) throws CDKException, CloneNotSupportedException {
        SmilesGenerator tmpGenerator = new SmilesGenerator(SmiFlavor.Unique);
        IAtomContainer tmpSchuffenhauerOriginal = this.getSchuffenhauerScaffold(aMolecule);
        int tmpRingCount = this.getRings(tmpSchuffenhauerOriginal, true).size();
        List<String> tmpAddedSMILESList = new ArrayList<>(tmpRingCount * 45);
        //List of all fragments already created and size estimated on the basis of an empirical value
        List<IAtomContainer> tmpIterativeRemovalList = new ArrayList<>(tmpRingCount * 45);
        tmpIterativeRemovalList.add(tmpSchuffenhauerOriginal); //Add origin SchuffenhauerScaffold
        for(int tmpCounter = 0 ; tmpCounter < tmpIterativeRemovalList.size(); tmpCounter++) {//Go through all the molecules created
            IAtomContainer tmpIterMol = tmpIterativeRemovalList.get(tmpCounter); //Take the next molecule from the list
            List<IAtomContainer> tmpAllRingsList = this.getRings(tmpIterMol,true);
            int tmpRingSize = tmpAllRingsList.size();
            for(IAtomContainer tmpRing : tmpAllRingsList) { //Go through all rings
                if(tmpRingSize < 2) { //Skip molecule if it has less than 2 rings
                    continue;
                }
                if(this.isRingTerminal(tmpIterMol, tmpRing)) { //Consider all terminal rings
                    boolean tmpIsInList = false;
                    IAtomContainer tmpRingRemoved = this.getSchuffenhauerScaffold(this.removeRing(tmpIterMol, tmpRing)); //Remove next ring
                    String tmpRingRemovedSMILES = tmpGenerator.create(tmpRingRemoved); //Generate unique SMILES
                    if(tmpAddedSMILESList.contains(tmpRingRemovedSMILES)) { //Check if the molecule has already been added to the list
                        tmpIsInList = true;
                    }
                    if(tmpIsInList == false) { //Add the molecule only if it is not already in the list
                        tmpIterativeRemovalList.add(tmpRingRemoved);
                        tmpAddedSMILESList.add(tmpRingRemovedSMILES);
                    }
                }
            }
        }
        return tmpIterativeRemovalList;
    }

    /**
     * Iteratively removes the terminal rings. All resulting Schuffenhauer scaffolds are saved in a tree.
     * A new level is created with each removal step. Duplicates are permitted.
     * The Schuffenhauer scaffold of the entire entered molecule is the root of the tree.
     * @param aMolecule Molecule to be disassembled.
     * @return List with all resulting Schuffenhauer scaffolds.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    public TreeNode<IAtomContainer> getRemovalTree(IAtomContainer aMolecule) throws CDKException, CloneNotSupportedException {
        IAtomContainer tmpSchuffenhauerOriginal = this.getSchuffenhauerScaffold(aMolecule);
        int tmpRingCount = this.getRings(tmpSchuffenhauerOriginal, true).size();
        //List of all fragments already created and size estimated on the basis of an empirical value
        List<IAtomContainer> tmpIterativeRemovalList = new ArrayList<>(tmpRingCount * 45);
        List<TreeNode> tmpAllNodesList = new ArrayList<>(); //List of all TreeNodes
        tmpIterativeRemovalList.add(tmpSchuffenhauerOriginal); //Add origin SchuffenhauerScaffold
        TreeNode<IAtomContainer> tmpParentNode = new TreeNode<IAtomContainer>(tmpSchuffenhauerOriginal); //Set origin Schuffenhauer as root
        tmpAllNodesList.add(tmpParentNode);
        int tmpLevelCounter = 0; //Shows which level of the tree we are currently on.
        for(int tmpCounter = 0 ; tmpCounter < tmpIterativeRemovalList.size(); tmpCounter++) { //Go through all the molecules created
            IAtomContainer tmpIterMol = tmpIterativeRemovalList.get(tmpCounter); //Take the next molecule from the list
            List<IAtomContainer> tmpRings = this.getRings(tmpIterMol,true);
            int tmpRingSize = tmpRings.size();
            for(IAtomContainer tmpRing : tmpRings) { //Go through all rings
                if(tmpRingSize < 2) { //Skip molecule if it has less than 2 rings
                    continue;
                }
                if(this.isRingTerminal(tmpIterMol, tmpRing)) { //Consider all terminal rings
                    IAtomContainer tmpRingRemoved = this.getSchuffenhauerScaffold(this.removeRing(tmpIterMol, tmpRing)); //Remove next ring
                    tmpAllNodesList.add(tmpAllNodesList.get(tmpLevelCounter).addChild(tmpRingRemoved)); //Add next node to current Level
                    tmpIterativeRemovalList.add(tmpRingRemoved); // The molecule added to the tree is added to the list
                }
            }
            tmpLevelCounter++; //Increases when a level is completed
        }
        return tmpParentNode;
    }
}