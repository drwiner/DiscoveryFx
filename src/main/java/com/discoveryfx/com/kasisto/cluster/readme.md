

# Cluster Service

Author: drw 2021

### Cluster Service

The cluster service takes sentences and produces a cluster report. Here is an example of a cluster service request with most of the clustering default settings for KCB default assistant.

```
curl --request POST \
  --url http://localhost:8090/kai/api/v1/cluster/run_job \
  --header 'Content-Type: application/json' \
  --header 'secret: ef576554-637f-11e8-adc0-fa7ae01bbebc' \
  --cookie 'JSESSIONID=4kdo6hcsck6ngcngggt7wz5j; JSESSIONID=node0ozmjrumukvsd1rj30nh7dnruf31582217727481aee61a71-6f52-4ed2-96cd-68bebb660477' \
  --data '{
	"assistant": "kcb",
	"target": "default_assistant",
	"job_id": "unique_label",
	"do_package_compare": true,
	"intents_to_hold_out": [
    		"kcb_feedback_binary",
    		"kcb_feedback_csat",
    		"kcb_feedback_survey",
    		"Feedbackfive",
    		"VpaJunk"
    	],
    	"num_training_data_per_intent_centroid": 10,
	"cluster_method": "STREAM",
	"min_cluster_size": 5,
	"input_file": "~/kai/tests/qa_tests/clustertest/mini_test.txt",
	"out_path": "~/kai/tests/qa_tests/clustertest/out_path/"
}'
```

In this example, the `input_file` field references a raw text file with 1 sentence per line, and the `out_path` references a folder to save the clustering report. Since the report can be fairly large, the response to the above request just indicates the status of the job. 


#### Cluster Service Request

The fields for the cluster service request are listed below directly from the class.

- `job_id` - A unique identifier for the job
- `assistant` - Name of the Tenant/App (e.g., "default_assistant")
- `target` - Specific assistant target (e.g., "default")
- `created_by` - Optional field that just passes to the report.
- `suppress_output` - Boolean, default=false, Whether to suppress the output cluster reports to the terminal.
- `input_file` - Path to file of data to cluster (a raw text file with 1 sentence per line).
- `input_data` - In place of `input_file`, user can also specify a list of sentences in the json body. (e.g., `input_data`: `["Sentence 1", "Sentence 1", ...]`
- `out_path` - Path to save report file.
- `intents_to_hold_out_from_traces` - Intents to exclude from data to cluster, when using Traces to get data.
- `intents_to_include_from_traces` - Intents to include from data to cluster, when using Traces to get data.
- `created_date` - String, the creation date of the report. Just passed to the report, not used during clustering.
- `from_date` - String of LocalDateTime, the start of date range of the original sentence requests, used just when data comes from Traces.
- `to_date` - String of LocalDateTime, the end of the date range of the original sentence requests, used just when data comes from Traces.
- `do_package_compare` - Boolean, whether the clustering report should include information about the closest intents relative to whatever data package is currently loaded.
- `intents_to_hold_out` - List<String> intent names to hold out from creating closest intents.
- `num_training_data_per_intent_centroid` - Integer, number of training data to include for computing the centroids for closest intents.
- `top_n` - Integer, default=3, The number of closest intents to include in the report for each cluster.
- `min_cluster_size`, Integer, default=25, The minimum size of a cluster to be included in a report. 
- `use_default_cluster_settings` - If true, will set parameters for clustering based on the input data.
- `cluster_method`, Enum, one of `"STREAM", "HDBSCAN", or "K_MEANS"`. Determines which clustering method will be used. It's recommended to use `HDBSCAN` for smaller data sets (< 3,000 sentences) and `STREAM` for larger.
------------------------------
#### STREAM Algorithm Parameters
- `purgatory_params` - Parameters used by the clustering algorithm.
```
"purgatory_params": {
   		"num_clusters_target": 16,
   		"batch_size": 4,
   		"eons": 4,
   		"sim_thresh": 0.8
   	}
```
- `num_clusters_target` - Target number of clusters to expect, used as a guideline by the `STREAM` algorithm. Should be about `.0008` * input size.
- `batch_size` - Parameter used by `STREAM` to evaluate candidate intents. Recommendations forthcoming.
- `eons` - Parameter used by `STREAM` to evaluate candidate intents, Recommendations forthcoming.
- `sim_thresh` - Parameter used by all clustering algorithms. It is the minimum similarity confidence for an utterance to be considered part of a cluster.
------------------------------
#### K_MEANS Algorithm Parameters
- `k_means_params` - Parameters used by the `K_MEANS` clustering algorithm.

```
"kmeans_params": {
   			"k": 16,
   			"max_it": 1000
   		}
```
- `k` - Integer, the target number of clusters to output by `K_MEANS`, prior to pruning (if applicable) by `min_cluster_size`.
- `max_it` - Integer, maximum number of iterations for `K_MEANS`, 
------------------------------
- `batch_size` - Integer, batch size for vectorizing sentences, depends on GPU limitations.
- `clear_models` - Boolean, By default the service will save any vectorized content. This field indicates whether or not the service should ignore any saved vectorized content and re-vectorize, such as when there are different settings for the word embeddign model.
- `use_fine_tuned` - Boolean, default=false, If true, a fine-tuned model will be used based on the data package currently installed. If no fine tuned model already exists, one will be created.
- `reduce_dimensions` - Integer, default=null, indicates whether to run Principal components Analysis (PCA) to reduce the dimensions of the vectors to smaller size. 
------------------------------
#### Gold Set Evaluation Parameters
- `gold_set_file` - A file containing the partition of sentences into clusters, representing a gold standard for comparison. Thus, it should contain the same sentences as given in `input_data` or `input_file`, though it's not required. The contents should be formatted in a JSON nested list like

```
[
    [
        "this is a sentence",
        "many sentences to come",
        "another setnence"
    ],
    [
        "anjother",
        "keep going",
        "need a good amount",
        "test more",
        "test"
    ]
]
```
- `gold_set` - Optionally, can put the gold set directly into the JSON body under this field.
- `evolutionary_algorithm` - Parameters for finding the best parameters for the gold set. Including this field kicks off the evolutionary algorithm. Initially a population of agents, each representing a clustering attempt, are created at random and assigned parameters from among provided choices. After all agents are evaluated, a new generation is evolved from the best performing ones. The maximum number of generations is hard coded at 100.
 
```
"evolutionary_algorithm" : {
    "num_generations_without_improvement_allowed": 10,
    "population_size": 10,
    "num_survival": 2,
    "num_crossover": 4,
    "num_mutate": 4,
    "method_gene": ["STREAM", "HDBSCAN"],
    "sim_thresh_gene": [0.74,  0.76, 0.78, 0.8, 0.82, 0.84, 0.86],
    "min_cluster_size_gene": [5, 10],
    "reduced_dim_gene": [64, 128, 256, 512, null],
    "fine_tune_gene": [false, true],
    "target_num_clusters_gene": [8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30],
    "stream_batch_size_gene": [4, 8, 10, 12, 14, 16, 18, 20, 24],
    "stream_eon_size_gene": [4, 8, 10, 12, 14, 16, 18, 20, 24]
}
```


- `num_generations_without_improvement_allowed` - Integer, default=6, number of generations without improvement to persist before termination.
- `population_size` - Integer, default=10, The number of agents in the initial population.
- `num_survival` - Integer, default=2, The number of best performers to persist to the next generation without modification.
- `num_crossover` - Integer, default=4, The number of agents to create for next generation that involves combining the genes of the survivors from the previous generation. 
- `num_mutate` - Integer, default=4, The number of agents to create for next generation that involves randomly mutating half the genes of a randomly selected survivor from the previous generation.

(Any of the below genes can be removed from the parameter list, in which case the default is set to whatever is set on the parent clustering request.)
- `method_gene` - Choice of which cluster method to use for an agent.
- `sim_thresh_gene` - Choice of which similarity threshold to use for an agent.
- `min_cluster_size_gene` - Choice of which minimum cluster size to use for an agent.
- `reduced_dim_gene` - Choice of which value to reduce dimensions for vectors, null is allowed meaning no reduction.
- `fine_tune_gene` - Choice of whether agent uses fine-tuned model or pretrained core model.
- `target_num_clusters_gene` - Choice of which `k` (if k-means) or `num_clusters_target` if `STREAM` an agent has.
- `stream_batch_size_gene` - Choice of which batch size an agent has if running `STREAM`
- `stream_eon_size_gene` - Chocie of which eon size an agent has if running `STREAM`


Ultimately, the full request with evolutionary algorithm would like something like:
```
{
	"assistant": "kcb",
	"target": "default_assistant",
	"job_id": "gold_mfc_evolution_test_kitsys3",
	"created_date": "2020-11-17-12:00:00Z",
	"from_date": "2020-09-17-12:00:00Z",
	"to_date": "2020-11-00-12:00:00Z",
	"unknown_only": false,
	"do_package_compare": false,
	"cluster_method": "STREAM",
	"min_cluster_size": 5,
	"reduce_dimensions": null,
	"purgatory_params": {
		"num_clusters_target": 16,
		"batch_size": 4,
		"eons": 4,
		"sim_thresh": 0.8,
		"is_adaptive": false
	},
	"kmeans_params": {
			"k": 16,
			"max_it": 1000
		},
	"clear_models": false,
	"use_fine_tuned": false,
	"evolutionary_algorithm" : {
		"num_generations_without_improvement_allowed": 10,
			"method_gene": ["STREAM", "HDBSCAN"],
			"sim_thresh_gene": [0.74,  0.76, 0.78, 0.8, 0.82, 0.84, 0.86],
			"min_cluster_size_gene": [5, 10],
			"reduced_dim_gene": [64, 128, 256, 512, null],
			"fine_tune_gene": [false, true],
			"target_num_clusters_gene": [8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30],
			"stream_batch_size_gene": [4, 8, 10, 12, 14, 16, 18, 20, 24],
			"stream_eon_size_gene": [4, 8, 10, 12, 14, 16, 18, 20, 24]
	},
	"input_file":"/home/drw/mfc_gold_test/mfc_cluster_test.txt",
	"gold_set_file":"/home/drw/mfc_gold_test/manual_gold_cluster.json",
	"out_path": "/home/drw/mfc_gold_test/evolve_results_mass/"
}

```

