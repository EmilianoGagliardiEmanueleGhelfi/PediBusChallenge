package solvers;

import GraphCore.Arc;
import GraphCore.GraphUtility;
import GraphCore.Vertex;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by emanueleghelfi on 25/01/2017.
 */
public class LocalSolverFromScratch implements SolverInterface {


    private SimpleWeightedGraph<Vertex, Arc> solution;
    private Vertex school;

    public LocalSolverFromScratch() {
        solution = new SimpleWeightedGraph<Vertex, Arc>(Arc.class);
        school = GraphUtility.getSchool();
        solution.addVertex(school);
    }


    @Override
    public SimpleWeightedGraph<Vertex, Arc> solve(SimpleWeightedGraph<Vertex, Arc> graph, double alpha, int iteration) {
        //all vertices connected to the school
        ArrayList<Vertex> vertices = new ArrayList<>();
        //all path not containing the school
        List<ArrayList<Vertex>> paths = new ArrayList<>();

        //find paths
        int i =0;
        for(Vertex v: graph.vertexSet()){
            paths.add(new ArrayList<>());
            paths.get(i).add(v);
            i++;
        }



        //now the paths array contains all paths from school
        int changes = 0;
        do{
            changes=0;
            for(int j = 0; j< paths.size();j++){
                //here we get the path j-th, check for all other path if is possible to add a node in path j
                //for other paths with more nodes
                for(int k = 0;k<paths.size();k++){
                    // if k-th paths is not equal to j-th and has a lower number of nodes
                    if(j!=k && paths.get(j).size()>=paths.get(k).size()){
                        // for every node in k-th path
                        for(int m = 0; m<paths.get(k).size();m++){
                            Vertex vertex = paths.get(k).get(m);
                            // check if it's possible to add it in some position of j-th path
                            for(int l=paths.get(j).size(); l>=0;l--){
                                if(GraphUtility.checkPathFeasible((ArrayList<Vertex>)paths.get(j).clone(),vertex,alpha,l)){
                                    paths.get(j).add(l,vertex);
                                    paths.get(k).remove(m);
                                    changes++;
                                    break;
                                }
                            }
                        }
                    }
                }
            }



        }while (changes!=0);

        solution = new SimpleWeightedGraph<Vertex, Arc>(Arc.class);
        solution.addVertex(school);
        //add all vertices in the path to the solution
        for(ArrayList<Vertex> path: paths){
            for (int j = 0; j < path.size(); j++) {
                solution.addVertex(path.get(j));
                if (j == 0) {
                    solution.addEdge(school, path.get(j));
                    solution.setEdgeWeight(solution.getEdge(school, path.get(j)),
                            GraphUtility.getDistanceFromSchool(path.get(j)));
                } else {
                    solution.addEdge(path.get(j - 1), path.get(j));
                    solution.setEdgeWeight(solution.getEdge(path.get(j - 1), path.get(j)),
                            path.get(j).computeDistance(path.get(j - 1)));
                }
            }
        }

        return solution;
    }
}
