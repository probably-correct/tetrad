package edu.cmu.tetrad.algcomparison.algorithm.cluster;

import edu.cmu.tetrad.algcomparison.algorithm.Algorithm;
import edu.cmu.tetrad.algcomparison.utils.HasKnowledge;
import edu.cmu.tetrad.algcomparison.utils.TakesInitialGraph;
import edu.cmu.tetrad.data.*;
import edu.cmu.tetrad.graph.EdgeListGraph;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.search.FindTwoFactorClusters;
import edu.cmu.tetrad.search.SearchGraphUtils;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.data.Dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * FTFC.
 *
 * @author jdramsey
 */
public class Ftfc implements Algorithm, TakesInitialGraph, HasKnowledge, ClusterAlgorithm {
    static final long serialVersionUID = 23L;
    private IKnowledge knowledge = new Knowledge2();

    public Ftfc() {}

    @Override
    public Graph search(DataModel dataSet, Parameters parameters) {
        ICovarianceMatrix cov = null;

        if (dataSet instanceof Dataset) {
            cov = DataUtils.getCovMatrix(dataSet);
        } else if (dataSet instanceof  ICovarianceMatrix){
            cov = (ICovarianceMatrix) dataSet;
        } else {
            throw new IllegalArgumentException("Expected a dataset or a covariance matrix.");
        }

        double alpha = parameters.getDouble("alpha");

        boolean gap = parameters.getBoolean("useGap", true);
        FindTwoFactorClusters.Algorithm algorithm;

        if (gap) {
            algorithm = FindTwoFactorClusters.Algorithm.GAP;
        } else {
            algorithm = FindTwoFactorClusters.Algorithm.SAG;
        }

        FindTwoFactorClusters search
                = new FindTwoFactorClusters(cov, algorithm, alpha);
        search.setVerbose(parameters.getBoolean("verbose"));

        return search.search();
    }

    @Override
    public Graph getComparisonGraph(Graph graph) {
        return SearchGraphUtils.patternForDag(new EdgeListGraph(graph));
    }

    @Override
    public String getDescription() {
        return "FTFC (Find Two Factor Clusters)";
    }

    @Override
    public DataType getDataType() {
        return DataType.Continuous;
    }

    @Override
    public List<String> getParameters() {
        List<String> parameters = new ArrayList<>();
        parameters.add("alpha");
        parameters.add("useWishart");
        parameters.add("useGap");
        parameters.add("verbose");
        return parameters;
    }

    @Override
    public IKnowledge getKnowledge() {
        return knowledge;
    }

    @Override
    public void setKnowledge(IKnowledge knowledge) {
        this.knowledge = knowledge;
    }

	/* (non-Javadoc)
	 * @see edu.cmu.tetrad.algcomparison.utils.TakesInitialGraph#getInitialGraph()
	 */
	@Override
	public Graph getInitialGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.tetrad.algcomparison.utils.TakesInitialGraph#setInitialGraph(edu.cmu.tetrad.graph.Graph)
	 */
	@Override
	public void setInitialGraph(Graph initialGraph) {
		// TODO Auto-generated method stub
		
	}
}
