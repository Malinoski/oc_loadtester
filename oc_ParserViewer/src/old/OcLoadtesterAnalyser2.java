package old;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OcLoadtesterAnalyser2 {

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void old(String ars[]) {
		
		// Data for CSV
		List<Object> header = new ArrayList<>();
		header.add("Users/Seconds");
		boolean createHeader = true; // This will run only to create the header (first group of elements)
		List<Object> lines = new ArrayList<>();			
		
		/////////////
		// Parameters
		String gatlingResultPath = ""; // gatling result path
		String finalFile = ""; // java result file path (csv)		
		if(ars.length != 0) {
			gatlingResultPath = ars[0];
			finalFile = ars[1];
		}else {
			gatlingResultPath = "/Users/iuri/gatling-results/gatling-results-v5";
			finalFile = "WebContent/gatling-results-v5.csv";
		}
		
		//////////////
		// PARSE BEGIN
		File resultFile = null;
		File[] resultPaths;
		try {
			resultFile = new File(gatlingResultPath);
			resultPaths = resultFile.listFiles();
			for (File resultPath : resultPaths) {

				// System.out.println(resultPath);
				File rampFile = null;
				File[] rampPaths;
				try {

					rampFile = new File(resultPath.getAbsolutePath());
					rampPaths = rampFile.listFiles();
					for (File rampPath : rampPaths) {

						File machineFile = null;
						File[] machinePaths;
						try {
							
							// Print and Save result in array
							System.out.println("#######################");
							System.out.print("Simulation:"+rampPath+"\n\n");
							
							String[] parts = rampPath.toString().split("/");
							List<Object> line = new ArrayList<>();
							line.add(parts[parts.length-2]+"/"+parts[parts.length-1]); // To get ramp0010 (size-2 index) user500 (size-1 index)
							
							machineFile = new File(rampPath.getAbsolutePath());
							machinePaths = machineFile.listFiles();
							
							for (File machinePath : machinePaths) {

								// Print 
								String machine = machinePath.getName();
								System.out.println("Machine:"+machine);								
								
								File simulationFile = null;
								File[] sinulationPaths;
								try {									

									simulationFile = new File(machinePath.getAbsolutePath());
									sinulationPaths = simulationFile.listFiles();
									int count = 1;
									for (File simulationPath : sinulationPaths) {
										
										System.out.println(simulationPath.getAbsoluteFile());										
										
										if(createHeader) {
											String temp = machine.substring(0,2);
											header.add(temp+count+"-Dur");
											header.add(temp+count+"-Tot");
											header.add(temp+count+"-Suc");
											header.add(temp+count+"-SucPerc");
											//System.out.println(temp+count);
											count++;
										}
											
										String duration = "N/A";
										Double ko = 0.0;
										Double ok = 0.0;
										Double totalRequest = 0.0;
										
										try {
											// TOTAL (duration)
											String line40 = Files.readAllLines(Paths.get(simulationPath.getAbsoluteFile() + "/index.html")).get(40);
											List<String> myList = new ArrayList<String>(Arrays.asList(line40.split(" ")));
											//System.out.println(myList.get(33));
											duration = myList.get(33);
											
											// GLOBAL
											String jsonContent = readFile(simulationPath + "/js/global_stats.json",StandardCharsets.UTF_8);
											Map<String, Object> map = new Gson().fromJson(jsonContent, new TypeToken<Map<String, Object>>() {}.getType());
											
											// numberOfRequests
											Map<String, Object> numberOfRequests = (Map<String, Object>)map.get("numberOfRequests");
											// KO
											ko = (double)numberOfRequests.get("ko"); 
											//failedArray.add(ko.intValue());										
											// OK
											ok = (double)numberOfRequests.get("ok");
											//successArray.add(ok.intValue());
											
											// TOTAL (numberOfRequests)
											totalRequest = (double)numberOfRequests.get("total");
											//totalRequestArray.add(totalRequest.intValue());
										} catch (NoSuchFileException e) {
											System.out.println("ERROR: index.html was not created yet!");
											// e.printStackTrace();
										}
										
										// Columns do CSV
										line.add(duration);
										line.add(totalRequest);
										line.add(ok);
										line.add( (ok*100)/totalRequest );
										
									}
									
									System.out.println();
									
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
							
							lines.add(line);							
							createHeader = false;

							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//////////////////////////////////////////////////////////////////////
		// Generate CSV from object
		PrintWriter writer = null;
		try {
			
			writer = new PrintWriter(finalFile, "UTF-8");		

			// Header
			for (int i = 0; i < header.size(); i++) {				
				writer.print(header.get(i));
					
				// the last 
				if(i < header.size()-1) {
					writer.print(",");
				}else {
					writer.println();
				}
				
			}		
			
			// Lines
			for (int i = 0; i < lines.size(); i++) {
				
				List<Object> l = (List<Object>) lines.get(i);
				for (int j = 0; j < l.size(); j++) {
					writer.print(l.get(j));
					
					// the last 
					if(j < l.size()-1) {
						writer.print(",");
					}else {
						writer.println();
					}
				}
				
			}	
		
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		if(writer!=null) {
			writer.close();
		}
			
		
	}
}
