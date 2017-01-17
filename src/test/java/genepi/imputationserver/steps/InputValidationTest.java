package genepi.imputationserver.steps;

import java.io.IOException;

import genepi.hadoop.common.WorkflowStep;
import genepi.imputationserver.util.WorkflowTestContext;
import junit.framework.TestCase;

public class InputValidationTest extends TestCase {

	public static final boolean VERBOSE = true;

	public void testWithWrongReferencePanel() throws IOException {

		String configFolder = "test-data/configs/hapmap-chr1";
		String inputFolder = "test-data/data/single";

		// create workflow context
		WorkflowTestContext context = buildContext(inputFolder, "missing-reference-panel", "shapeit");

		// create step instance
		InputValidation inputValidation = new InputValidationMock(configFolder);

		//run and test
		boolean result = run(context, inputValidation);

		// check if step is failed
		assertEquals(false, result);

		// check error message
		assertTrue(context.hasInMemory("[ERROR] Reference 'missing-reference-panel' not found."));

	}
	
	public void testWithInnsufficientSamples() throws IOException {

		String configFolder = "test-data/configs/hapmap-chr1";
		String inputFolder = "test-data/data/single";

		// create workflow context
		WorkflowTestContext context = buildContext(inputFolder, "hapmap2", "shapeit");

		// create step instance
		InputValidation inputValidation = new InputValidationMock(configFolder);

		//run and test
		boolean result = run(context, inputValidation);

		// check if step is failed
		assertEquals(false, result);

		// check analyze task
		assertTrue(context.hasInMemory("[RUN] Analyze file minimac_test.50.vcf.gz"));

		// check error message
		assertTrue(context.hasInMemory("[ERROR] At least 50 samples must be included"));

	}

	public void testWrongFiles() throws IOException {

		String configFolder = "test-data/configs/hapmap-chr1";
		//input folder contains no vcf or vcf.gz files
		String inputFolder = "test-data/data/wrong_files";

		// create workflow context
		WorkflowTestContext context = buildContext(inputFolder, "hapmap2", "eagle");

		// create step instance
		InputValidation inputValidation = new InputValidationMock(configFolder);

		//run and test
		boolean result = run(context, inputValidation);

		// check if step is failed
		assertEquals(false, result);

		// check error message
		assertTrue(context.hasInMemory("[ERROR] The provided files are not VCF files"));

	}
	
	public void testWrongVcfFile() throws IOException {

		String configFolder = "test-data/configs/hapmap-chr1";
		//input folder contains no vcf or vcf.gz files
		String inputFolder = "test-data/data/wrong_vcf";

		// create workflow context
		WorkflowTestContext context = buildContext(inputFolder, "hapmap2", "eagle");

		// create step instance
		InputValidation inputValidation = new InputValidationMock(configFolder);

		//run and test
		boolean result = run(context, inputValidation);

		// check if step is failed
		assertEquals(false, result);

		// check error message
		assertTrue(context.hasInMemory("[ERROR] Unable to parse header with error"));

	}
	
	public void testUnorderedVcfFile() throws IOException {

		String configFolder = "test-data/configs/hapmap-chr1";
		//input folder contains no vcf or vcf.gz files
		String inputFolder = "test-data/data/unorderd";

		// create workflow context
		WorkflowTestContext context = buildContext(inputFolder, "hapmap2", "eagle");

		// create step instance
		InputValidation inputValidation = new InputValidationMock(configFolder);

		//run and test
		boolean result = run(context, inputValidation);

		// check if step is failed
		assertEquals(false, result);

		// check error message
		assertTrue(context.hasInMemory(" [ERROR] The provided VCF file is malformed"));
		assertTrue(context.hasInMemory(" Error during index creation"));

	}
	
	
	
	public void testWrongChromosomes() throws IOException {

		String configFolder = "test-data/configs/hapmap-chr1";
		String inputFolder = "test-data/data/wrong_chrs";

		// create workflow context
		WorkflowTestContext context = buildContext(inputFolder, "hapmap2", "eagle");

		// create step instance
		InputValidation inputValidation = new InputValidationMock(configFolder);

		//run and test
		boolean result = run(context, inputValidation);

		// check if step is failed
		assertEquals(false, result);

		// check analyze task
		assertTrue(context.hasInMemory("[RUN] Analyze file minimac_test.50.vcf.gz"));

		// check error message
		assertTrue(context.hasInMemory("[ERROR] The provided VCF file contains more than one chromosome."));

	}
	
	public void testSingleUnphasedVcfWithEagle() throws IOException {

		String configFolder = "test-data/configs/hapmap-chr1";
		String inputFolder = "test-data/data/single";

		// create workflow context
		WorkflowTestContext context = buildContext(inputFolder, "hapmap2", "eagle");

		// create step instance
		InputValidation inputValidation = new InputValidationMock(configFolder);

		//run and test
		boolean result = run(context, inputValidation);


		// check if step is failed
		assertEquals(true, result);

		// check analyze task and results
		assertTrue(context.hasInMemory("[RUN] Analyze file minimac_test.50.vcf.gz"));
		assertTrue(context.hasInMemory("[OK] 1 valid VCF file(s) found."));

		// check statistics
		assertTrue(context.hasInMemory("Samples: 41"));
		assertTrue(context.hasInMemory("Chromosomes: 1"));
		assertTrue(context.hasInMemory("SNPs: 905"));
		assertTrue(context.hasInMemory("Chunks: 1"));
		assertTrue(context.hasInMemory("Datatype: unphased"));
		assertTrue(context.hasInMemory("Reference Panel: hapmap2"));
		assertTrue(context.hasInMemory("Phasing: eagle"));

	}
	
	public void testThreeUnphasedVcfWithEagle() throws IOException {

		String configFolder = "test-data/configs/hapmap-chr1";
		String inputFolder = "test-data/data/three";
		// create workflow context
		WorkflowTestContext context = buildContext(inputFolder, "hapmap2", "eagle");

		// create step instance
		InputValidation inputValidation = new InputValidationMock(configFolder);

		//run and test
		boolean result = run(context, inputValidation);


		// check if step is failed
		assertEquals(true, result);

		// check analyze task and results
		assertTrue(context.hasInMemory("[RUN] Analyze file minimac_test2.50.vcf.gz"));
		assertTrue(context.hasInMemory("[RUN] Analyze file minimac_test3.50.vcf.gz"));
		assertTrue(context.hasInMemory("[RUN] Analyze file minimac_test4.50.vcf.gz"));
		assertTrue(context.hasInMemory("[OK] 3 valid VCF file(s) found."));

		// check statistics
		assertTrue(context.hasInMemory("Samples: 41"));
		assertTrue(context.hasInMemory("Chromosomes: 2 3 4"));
		assertTrue(context.hasInMemory("SNPs: 2715"));
		assertTrue(context.hasInMemory("Chunks: 3"));
		assertTrue(context.hasInMemory("Datatype: unphased"));
		assertTrue(context.hasInMemory("Reference Panel: hapmap2"));
		assertTrue(context.hasInMemory("Phasing: eagle"));

	}

	class InputValidationMock extends InputValidation {

		private String folder;

		public InputValidationMock(String folder) {
			super();
			this.folder = folder;
		}

		@Override
		public String getFolder(Class clazz) {
			// override folder with static folder instead of jar location
			return folder;
		}

	}

	
	protected boolean run(WorkflowTestContext context, WorkflowStep step) {
		step.setup(context);
		return step.run(context);
	}

	protected WorkflowTestContext buildContext(String folder, String referencePanel, String phasing) {
		WorkflowTestContext context = new WorkflowTestContext();
		context.setVerbose(VERBOSE);
		context.setInput("files", folder);
		context.setInput("refpanel", referencePanel);
		context.setInput("population", "eur");
		context.setInput("phasing", phasing);
		context.setInput("sample-limit", "0");
		context.setInput("chunksize", "10000000");
		return context;

	}

}