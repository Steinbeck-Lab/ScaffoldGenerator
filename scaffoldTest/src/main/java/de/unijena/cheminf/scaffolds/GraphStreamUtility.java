/*
 * Copyright (c) 2021 Julian Zander, Jonas Schaub, Achim Zielesny
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

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.interfaces.IAtomContainer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Contains useful functions such as visualisation with Graphstream.
 *
 * @version 1.0
 */
public final class GraphStreamUtility {
    /**
     * The tree is displayed in a window with GraphStream. The numbering of the nodes reflects the respective level.
     * @param aScaffoldTree displayed Tree
     * @throws Exception if anything goes wrong
     */
    public static void displayTreeWithGraphStream(ScaffoldTree aScaffoldTree) throws Exception {
        /*Create a graph from the ScaffoldTree*/
        Graph tmpGraph = new SingleGraph("TestGraph");
        tmpGraph.setAttribute("ui.stylesheet", "node { shape: rounded-box; size-mode: fit; padding: 60px; } graph { shape: box; size-mode: fit; padding: 70px; }");
        tmpGraph.setAttribute("ui.quality");
        tmpGraph.setAttribute("ui.antialias");
        System.setProperty("org.graphstream.ui", "swing");
        /*Add edges and nodes*/
        int tmpEdgeCount = 0;
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        Integer[][] tmpMatrix = aScaffoldTree.getMatrix(); //Create the adjacency matrix
        for(int tmpRow = 0; tmpRow < tmpMatrix.length; tmpRow++) { //Create a node for each row
            /*Add the ScaffoldTree nodes to the graph*/
            tmpGraph.addNode(String.valueOf(tmpRow));
            Node tmpNode = tmpGraph.getNode(String.valueOf(tmpRow));
            tmpNode.setAttribute("Node", aScaffoldTree.getMatrixNode(tmpRow));
            /*Add a label to each node that corresponds to the level in the tree. 0 is the root.*/
            TreeNode tmpTreeLevelNode = (TreeNode) aScaffoldTree.getMatrixNode(tmpRow);
            tmpNode.setAttribute("ui.label", tmpTreeLevelNode.getLevel());
            /*Add the images*/
            TreeNode tmpTreeNode = (TreeNode) aScaffoldTree.getMatrixNode(aScaffoldTree.getMatrixNodesNumbers().get(tmpRow));
            IAtomContainer tmpTreeNodeMolecule = (IAtomContainer) tmpTreeNode.getMolecule();
            BufferedImage tmpNodeImg = tmpGenerator.withSize(512,512).depict(tmpTreeNodeMolecule).toImg();
            /*The images are stored temporarily*/
            new File(System.getProperty("user.dir") + "//target/test-classes/GraphStream" + tmpRow + ".png").mkdirs();
            File tmpSecOutputRemove = new File(System.getProperty("user.dir") + "//target/test-classes/GraphStream" + tmpRow + ".png");
            ImageIO.write(tmpNodeImg, "png", tmpSecOutputRemove);
            //set the images
            tmpNode.setAttribute("ui.style", "fill-mode: image-scaled-ratio-max;" + "fill-image: url('GraphStream" + tmpRow + ".png');");
            /*Add edges*/
            for(int tmpCol = 0; tmpCol < tmpMatrix[tmpRow].length; tmpCol++) { //Go through each column of the row
                if(tmpRow < tmpCol) { //Skip a diagonal half to get edges in one direction only.
                    continue;
                }
                if(tmpMatrix[tmpRow][tmpCol] == 1) { //Insert an edge if there is a 1 in it
                    tmpGraph.addEdge("Edge" + tmpEdgeCount, tmpRow, tmpCol);
                    tmpEdgeCount++;
                }
            }
        }
        /*Display graph*/
        System.setProperty("org.graphstream.ui", "swing");
        tmpGraph.display();
        TimeUnit.SECONDS.sleep(300);
    }

    /**
     * The Network is displayed in a window with GraphStream. The numbering of the nodes reflects the respective level.
     * @param aScaffoldNetwork displayed Network
     * @throws Exception if anything goes wrong
     */
    public static void displayNetworkWithGraphStream(ScaffoldNetwork aScaffoldNetwork) throws Exception {
        /*Create a graph from the ScaffoldNetwork*/
        Graph tmpGraph = new SingleGraph("TestGraph");
        tmpGraph.setAttribute("ui.stylesheet", "node { shape: rounded-box; size-mode: fit; padding: 60px; } graph { shape: box; size-mode: fit; padding: 70px; }");
        tmpGraph.setAttribute("ui.quality");
        tmpGraph.setAttribute("ui.antialias");
        System.setProperty("org.graphstream.ui", "swing");
        /*Add edges and nodes*/
        int tmpEdgeCount = 0;
        DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(512,512).withFillToFit();
        Integer[][] tmpMatrix = aScaffoldNetwork.getMatrix(); //Create the adjacency matrix
        for(int tmpRow = 0; tmpRow < tmpMatrix.length; tmpRow++) { //Create a node for each row
            /*Add the ScaffoldNetwork nodes to the graph*/
            tmpGraph.addNode(String.valueOf(tmpRow));
            Node tmpNode = tmpGraph.getNode(String.valueOf(tmpRow));
            tmpNode.setAttribute("Node", aScaffoldNetwork.getMatrixNode(tmpRow));
            /*Add a label to each node that corresponds to the level in the tree. 0 is the root*/
            NetworkNode tmpNetworkNodeRow = (NetworkNode) aScaffoldNetwork.getMatrixNode(tmpRow);
            tmpNode.setAttribute("ui.label", tmpNetworkNodeRow.getLevel());
            /*Add the images*/
            NetworkNode tmpNetworkNode = (NetworkNode) aScaffoldNetwork.getMatrixNode(aScaffoldNetwork.getMatrixNodesNumbers().get(tmpRow));
            IAtomContainer tmpNetworkNodeMolecule = (IAtomContainer) tmpNetworkNode.getMolecule();
            BufferedImage tmpNodeImg = tmpGenerator.withSize(512,512).depict(tmpNetworkNodeMolecule).toImg();
            /*The images are stored temporarily*/
            new File(System.getProperty("user.dir") + "//target/test-classes/GraphStream" + tmpRow + ".png").mkdirs();
            File tmpSecOutputRemove = new File(System.getProperty("user.dir") + "//target/test-classes/GraphStream" + tmpRow + ".png");
            ImageIO.write(tmpNodeImg, "png", tmpSecOutputRemove);
            //set the images
            tmpNode.setAttribute("ui.style", "fill-mode: image-scaled-ratio-max;" + "fill-image: url('GraphStream" + tmpRow + ".png');");
            /*Add edges*/
            for(int tmpCol = 0; tmpCol < tmpMatrix[tmpRow].length; tmpCol++) { //Go through each column of the row
                if(tmpRow < tmpCol) { //Skip a diagonal half to get edges in one direction only.
                    continue;
                }
                if(tmpMatrix[tmpRow][tmpCol] == 1) { //Insert an edge if there is a 1 in it
                    tmpGraph.addEdge("Edge" + tmpEdgeCount, tmpRow, tmpCol);
                    tmpEdgeCount++;
                }
            }
        }
        /*Display graph*/
        System.setProperty("org.graphstream.ui", "swing");
        tmpGraph.display();
        TimeUnit.SECONDS.sleep(300);
    }
}
