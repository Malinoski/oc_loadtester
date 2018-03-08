package old;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OcDataGenerator {

	public static void old(String ars[]) {
		
		/////////////
		// Parameters
		String gatlingGeneratedDtaPath = ""; // gatling result web pages (html,css,json, etc)
		String bruteDataFile = ""; // csv brute data to be generated
		String processedDataFile = ""; // csv process data to be generated
		if(ars.length != 0) {
			gatlingGeneratedDtaPath = ars[0];
			bruteDataFile = ars[1];
			processedDataFile = ars[2];
		}else {
			gatlingGeneratedDtaPath = "/Users/iuri/gatling-results/gatling-results_v8";
			bruteDataFile = "data/gatling-data-v8-brute-v2.csv";
			processedDataFile = "data/gatling-data-v8-processed-v2.csv";
		}
		
		//////////////
		// PARSE BEGIN
		List<Object> lines = new ArrayList<>();	
		File resultFile = null;
		File[] resultPaths;
		try {
			resultFile = new File(gatlingGeneratedDtaPath);
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
													
							String ramp = (parts[parts.length-2]).substring(1,(parts[parts.length-2]).length());
							String users = (parts[parts.length-1]).substring(1,(parts[parts.length-1]).length());							
							
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
									
									for (File simulationPath : sinulationPaths) {
										List<Object> line = new ArrayList<>();	
										
										System.out.println(simulationPath.getAbsoluteFile());										
										
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
										if(ok>0) {
											line.add(users);
											line.add(ramp);
											line.add(machine);
											line.add(duration);
											line.add(totalRequest);
											line.add(ok);
											line.add( (ok*100)/totalRequest );
											lines.add(line);
										}
												
										
									}
									
									System.out.println();
									
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
							
												
//							createHeader = false;

							
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
		
		writeBruteCsv(lines,bruteDataFile);
		writeProcessedCsv(lines,processedDataFile);
			
		
	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static void writeBruteCsv(List<Object> lines, String bruteDataFile) {
		try {
			
			// Header
			List<Object> header = new ArrayList<>();
			header.add("# Users");
			header.add("Ramp");
			header.add("Machine");
			header.add("Duration");
			header.add("Total requests");
			header.add("Success requests");
			header.add("Success percent");
			
			PrintWriter writer = null;
			writer = new PrintWriter(bruteDataFile, "UTF-8");		

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
			if(writer!=null) {
				writer.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void writeProcessedCsv(List<Object> lines, String file) {
		try {
			
			// How many simulations has for each ramp time, user number and machine type?
			// Ex.: 100/10/vm 100/10/vm 100/10/vm =>>> result is 3
			int howManySimulations = 0;
			List<Object> lTemp = (List<Object>) lines.get(0);
			String usersTemp = (String)lTemp.get(0);
			String rampTemp = (String)lTemp.get(1);
			String machineTemp = (String)lTemp.get(2);
			String simTemp = usersTemp+"/"+rampTemp+"/"+machineTemp;			
			for (int i = 0; i < lines.size(); i++) {
				List<Object> lTemp2 = (List<Object>) lines.get(i);
				String usersTemp2 = (String)lTemp2.get(0);
				String rampTemp2 = (String)lTemp2.get(1);
				String machineTemp2 = (String)lTemp2.get(2);
				String simTemp2 = usersTemp2+"/"+rampTemp2+"/"+machineTemp2;
				if(simTemp2.equals(simTemp)) {
					howManySimulations++;
				}
			}
			System.out.println("How many simulations has for each ramp time, user number and machine type? "+howManySimulations);
			
			// Create header
			List<String> header = new ArrayList<>();
			
			header.add("# conf");
			
			header.add("v-duration");
			header.add("v-total-requests");
			header.add("v-success-num-requests");
			header.add("v-success-perc-requests");
			header.add("v-desv");
			
			header.add("c-duration");
			header.add("c-total-requests");
			header.add("c-success-num-requests");
			header.add("c-success-perc-requests");
			header.add("c-desv");
			
			PrintWriter writer = null;
			writer = new PrintWriter(file, "UTF-8");		

			for (int i = 0; i < header.size(); i++) {				
				writer.print(header.get(i));
					
				if(i < header.size()-1) {
					writer.print(",");
				}else {
					writer.println();
				}				
			}
			
			// Create Lines
			Map<String,List <Double>> lineMap = new HashMap<String,List <Double>>();
			
			for (int i = 0; i < lines.size(); i++) {
				
				List<Object> l = (List<Object>) lines.get(i);
				//System.out.println(l);
				
				String users = (String)l.get(0);
				String ramp = (String)l.get(1);
				String usersRamps = users+"/"+ramp;
				String machineType = (String)l.get(2);
				
				List <Double> temp = lineMap.get(usersRamps);;
				if (temp==null) {
					temp = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
				}
				
				// Duration
				Double duration = Double.valueOf( ((String)l.get(3)));
				if(machineType.equals("vm")) {
					temp.set(0, temp.get(0)+duration);
				}else if(machineType.equals("container")) {
					temp.set(5, temp.get(5)+duration);
				}
				
				// Total requests
				Double totalReq = (Double)l.get(4);
				if(machineType.equals("vm")) {
					temp.set(1, temp.get(1)+totalReq);
				}else if(machineType.equals("container")) {
					temp.set(6, temp.get(6)+totalReq);
				}
				
				// Success requests
				Double successReq = (Double)l.get(5);
				if(machineType.equals("vm")) {
					temp.set(2, temp.get(2)+successReq);
				}else if(machineType.equals("container")) {
					temp.set(7, temp.get(7)+successReq);
				}
				
				// Success Percent Requests
				Double successPercentReq = (Double)l.get(6);
				if(machineType.equals("vm")) {
					temp.set(3, temp.get(3)+successPercentReq);
				}else if(machineType.equals("container")) {
					temp.set(8, temp.get(8)+successPercentReq);
				}
				
				//System.out.println(temp);
				lineMap.put(usersRamps, temp);
				
			}	
			
			// Ordering (bubble sort)
			List<String> stringKeys = new ArrayList(lineMap.keySet());
			Map<Integer, String> lineMapOrderedTemp = new TreeMap<>();
			for (String key : stringKeys) {
				String[] parts = key.split("/");
				String usersString = parts[0];
				int usersInterger = Integer.parseInt(usersString);				
				lineMapOrderedTemp.put(usersInterger,key);
			}
			System.out.println(lineMapOrderedTemp);
			
			for (Integer integerKey : lineMapOrderedTemp.keySet()) {
				
				String key = lineMapOrderedTemp.get(integerKey);
				
				writer.print(key);
				writer.print(",");
				
				int i = 0;
				List<Double> value = lineMap.get(key);
				
				for(Double d: value) {
					writer.print(d/howManySimulations);
					// the last 
					if(i < value.size()-1) {
						writer.print(",");
					}else {
						writer.println();
					}	
					i++;					
				}
			}
			
			
			if(writer!=null) {
				writer.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
