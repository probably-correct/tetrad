package edu.cmu.tetrad.algcomparison.algorithm.multi;

import edu.cmu.tetrad.algcomparison.algorithm.MultiDataSetAlgorithm;
import edu.cmu.tetrad.algcomparison.algorithm.oracle.pattern.Fges;
import edu.cmu.tetrad.algcomparison.score.SemBicScore;
import edu.cmu.tetrad.algcomparison.utils.HasKnowledge;
import edu.cmu.tetrad.data.*;
import edu.cmu.tetrad.graph.EdgeListGraph;
import edu.cmu.tetrad.search.IndTestScore;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.algo.resampling.GeneralResamplingTest;
import edu.pitt.dbmi.algo.resampling.ResamplingEdgeEnsemble;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.search.SearchGraphUtils;
import edu.cmu.tetrad.search.SemBicScoreImages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wraps the tsIMaGES algorithm for continuous variables.
 * </p>
 * Requires that the parameter 'randomSelectionSize' be set to indicate how many
 * datasets should be taken at a time (randomly). This cannot given multiple values.
 *
 * @author jdramsey
 * @author dmalinsky
 */
public class TsImagesSemBic implements MultiDataSetAlgorithm, HasKnowledge {
    static final long serialVersionUID = 23L;
    private IKnowledge knowledge = new Knowledge2();

    public TsImagesSemBic() {
    }

    @Override
    public Graph search(List<DataModel> dataModels, Parameters parameters) {
    	if (parameters.getInt("numberResampling") < 1) {
            List<DataSet> dataSets = new ArrayList<>();

            for (DataModel dataModel : dataModels) {
                dataSets.add((DataSet) dataModel);
            }

            edu.cmu.tetrad.search.TsGFci search = new edu.cmu.tetrad.search.TsGFci(new IndTestScore(
                    new SemBicScoreImages(dataModels)), new SemBicScoreImages(dataModels));
            search.setFaithfulnessAssumed(true);
            search.setKnowledge(knowledge);

            return search.search();
    	}else{
    		TsImagesSemBic tsImagesSemBic = new TsImagesSemBic();
    		
    		List<DataSet> datasets = new ArrayList<>();

			for (DataModel dataModel : dataModels) {
				datasets.add((DataSet) dataModel);
			}
			GeneralResamplingTest search = new GeneralResamplingTest(datasets, tsImagesSemBic, parameters.getInt("numberResampling"));
			search.setKnowledge(knowledge);

			search.setPercentResampleSize(parameters.getDouble("percentResampleSize"));
            search.setResamplingWithReplacement(parameters.getBoolean("resamplingWithReplacement"));
            
            ResamplingEdgeEnsemble edgeEnsemble = ResamplingEdgeEnsemble.Highest;
            switch (parameters.getInt("resamplingEnsemble", 1)) {
                case 0:
                    edgeEnsemble = ResamplingEdgeEnsemble.Preserved;
                    break;
                case 1:
                    edgeEnsemble = ResamplingEdgeEnsemble.Highest;
                    break;
                case 2:
                    edgeEnsemble = ResamplingEdgeEnsemble.Majority;
            }
			search.setEdgeEnsemble(edgeEnsemble);
			search.setParameters(parameters);
			search.setVerbose(parameters.getBoolean("verbose"));
			return search.search();
    	}
    }

    @Override
    public Graph search(DataModel dataSet, Parameters parameters) {
    	if (parameters.getInt("numberResampling") < 1) {
            return search(Collections.singletonList((DataModel) DataUtils.getContinuousDataSet(dataSet)), parameters);
    	}else{
    		TsImagesSemBic tsImagesSemBic = new TsImagesSemBic();
    		
    		List<DataSet> dataSets = Collections.singletonList(DataUtils.getContinuousDataSet(dataSet));
    		GeneralResamplingTest search = new GeneralResamplingTest(dataSets, tsImagesSemBic, parameters.getInt("numberResampling"));
    		search.setKnowledge(knowledge);
			
    		search.setPercentResampleSize(parameters.getDouble("percentResampleSize"));
            search.setResamplingWithReplacement(parameters.getBoolean("resamplingWithReplacement"));
            
            ResamplingEdgeEnsemble edgeEnsemble = ResamplingEdgeEnsemble.Highest;
            switch (parameters.getInt("resamplingEnsemble", 1)) {
                case 0:
                    edgeEnsemble = ResamplingEdgeEnsemble.Preserved;
                    break;
                case 1:
                    edgeEnsemble = ResamplingEdgeEnsemble.Highest;
                    break;
                case 2:
                    edgeEnsemble = ResamplingEdgeEnsemble.Majority;
            }
			search.setEdgeEnsemble(edgeEnsemble);
			search.setParameters(parameters);
			search.setVerbose(parameters.getBoolean("verbose"));
			return search.search();
    	}
    }

    @Override
    public Graph getComparisonGraph(Graph graph) {
        return SearchGraphUtils.patternForDag(new EdgeListGraph(graph));
    }

    @Override
    public String getDescription() {
        return "tsIMaGES for continuous variables (using the SEM BIC score)";
    }

    @Override
    public DataType getDataType() {
        return DataType.Continuous;
    }

    @Override
    public List<String> getParameters() {
        List<String> parameters = new Fges(new SemBicScore(), false).getParameters();
        parameters.add("randomSelectionSize");
        // Resampling
        parameters.add("numberResampling");
        parameters.add("percentResampleSize");
        parameters.add("resamplingWithReplacement");
        parameters.add("resamplingEnsemble");
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
}
