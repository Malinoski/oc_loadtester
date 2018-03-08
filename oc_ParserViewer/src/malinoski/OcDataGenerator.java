package malinoski;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OcDataGenerator {

	// Brute file Header
	public static List<Object> header = new ArrayList<>();
	
	
	public static void main(String ars[]) {
		
		header.add("# Users");
		header.add("Ramp");
		header.add("Machine");
		header.add("Duration");
		header.add("Total requests");
		header.add("Success requests");
		header.add("Success percent");
		header.add("Success Variance (Valor menos media total)");
		header.add("Success Squared Variance");
		header.add("Duration Variance (Valor menos media total)");
		header.add("Duration Squared Variance");
		
		/////////////
		// Parameters
		String gatlingGeneratedDtaPath = ""; 	// gatling result web pages (html,css,json, etc)
		String bruteDataFile = ""; 				// csv brute data to be generated
		String processedContainerFile = ""; 		// csv container process data to be generated
		String processedVmFile = ""; 			// csv vm process data to be generated
		if(ars.length != 0) {
			gatlingGeneratedDtaPath = ars[0];
			bruteDataFile = ars[1];
			processedContainerFile = ars[2];
			processedContainerFile = ars[3];
		}else {
			gatlingGeneratedDtaPath = "/Users/iuri/gatling-results/ro30ra60us100-200-400-600-800-1000";
			bruteDataFile = "data/ro30ra60us100-200-400-600-800-1000-brute.csv";
			processedContainerFile = "data/ro30ra60us100-200-400-600-800-1000-processed-cont.csv";
			processedVmFile = "data/ro30ra60us100-200-400-600-800-1000-processed-vm.csv";
		}
		
		System.out.println("Start!");
		writeBruteCsv(bruteDataFile,gatlingGeneratedDtaPath);
		addDevioPadrao(bruteDataFile);
		writeProcessedCsv(bruteDataFile,processedContainerFile, processedVmFile);
		System.out.println("Finish!");
		
	}
	
	private static void addDevioPadrao(String bruteFile) {
		
		System.out.println("################# Adding pattern deviation to brute file");
		
		/** Read brute csv and save to a complete map */
		HashMap<String,List<List<Double>>> completeMap = new HashMap<String, List<List<Double>>>();
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(bruteFile));
			String line =  null;
			
			while((line=br.readLine())!=null){
		    	
		    		if(!line.startsWith("#")) {
		    			String str[] = line.split(",");
			        String key = str[0]+"/"+str[1]+"/"+str[2];
			        
			        List <Double> temp = new ArrayList<Double>();
			        	temp.add(Double.parseDouble(str[3])); 	//  Duration
			        	temp.add(Double.parseDouble(str[4]));	// Total request
			        	temp.add(Double.parseDouble(str[5]));	// Success request
			        	temp.add(Double.parseDouble(str[6]));	// Success request percent
			        	
			        List<List<Double>> tempList = completeMap.get(key);
			        if(tempList == null) {	
			        		tempList = new ArrayList<List<Double>>();
			        }
			        tempList.add(temp);
			        
			        completeMap.put(key, tempList);
		    		}
		        
		    }
			
			if(br!=null) {
				br.close();
			}
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/** Success Pattern Deviation Calculation*/		
		
		HashMap<String, Double> successAverageList = new HashMap(); // Total success average
		HashMap<String, Double> durationAverageList = new HashMap(); // Total success average
		
		for (String completeMapkey : completeMap.keySet()) {
			
			double totalSuccess = 0.0;
			double totalDuration = 0.0;
			//System.out.println(completeMapkey);
			List<List<Double>> list = completeMap.get(completeMapkey);			
			for(List<Double> l :list) {
				//System.out.println(l);
				totalSuccess += l.get(3);
				totalDuration += l.get(0);
				
			}			
			//System.out.println("totalSuccess:"+totalSuccess+" totalDuration:"+totalDuration+" list.size():"+list.size());
			
			double successAverage = totalSuccess/list.size();
			double durationAverage = totalDuration/list.size();
			//System.out.println("successAverage:"+successAverage+" durationAverage:"+durationAverage+"\n");
						
			successAverageList.put(completeMapkey, successAverage);
			durationAverageList.put(completeMapkey, durationAverage);
		}
		
		/** Add deviation in list */
		for (String completeMapkey : completeMap.keySet()) {
			
			// System.out.println(completeMapkey);			
			List<List<Double>> list = completeMap.get(completeMapkey);
			
			double averageSuccess = successAverageList.get(completeMapkey);
			double averageDuration = durationAverageList.get(completeMapkey);
			
			double value = 0;
			double variance = 0;
			for(List<Double> l :list) {
				
				// Success pattern deviation calc
				value = l.get(3);
				variance = value-averageSuccess;
				l.add(variance);
				l.add(variance*variance); // Variancia ao quadrado
				// System.out.println(value+" "+averageSuccess+" "+variance);
				 
				// Duration pattern deviation calc
				value = l.get(0);
				variance = value-averageDuration;
				l.add(variance);
				l.add(variance*variance); // Variancia ao quadrado
				// System.out.println(value+" "+averageDuration+" "+variance+" "+(variance*variance));
				
				// System.out.println(l);

			}
			
			//System.out.println(list);		
			
		}	
		
		/** Salvar em arquivo */
		try {
			
			PrintWriter writer = null;
			writer = new PrintWriter(bruteFile, "UTF-8");	
			writer.println("# "+bruteFile);

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
			for (String completeMapkey : completeMap.keySet()) {
				
				String parts[] = completeMapkey.split("/");
								
				for(List<Double> l :completeMap.get(completeMapkey)) {
					
					//System.out.print(parts[0]+","+parts[1]+","+parts[2]+",");
					writer.print(parts[0]+","+parts[1]+","+parts[2]+",");
					
					int cont = 0;
					for(Double d :l) {
						// System.out.print(d);
						writer.print(d);
						
						if(cont<l.size()-1) {
							// System.out.print(",");
							writer.print(",");
						}else {
							//System.out.println();
							writer.println();							
						}
						cont++;
					}
					// System.out.println();
					// writer.println();
				}				
				// System.out.println();
				//writer.println();
				
			}	
			
			if(writer!=null) {
				writer.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static void writeBruteCsv(String bruteDataFile, String gatlingGeneratedDtaPath) {
		
		System.out.println("################# Creating Brute CSV");
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
		
		/** Salvar em arquivo */
		try {
			
			PrintWriter writer = null;
			writer = new PrintWriter(bruteDataFile, "UTF-8");		
			writer.println("# "+gatlingGeneratedDtaPath);
			
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
	
	public static void writeProcessedCsv(String bruteFile, String processedContainerFile, String processedVmFile) {
		
		System.out.println("################# Creating processed CSV files (for container and vm)");
		
		/** Mapping to calc: Read brute csv and save to a complete map */
		HashMap<String,List<List<Double>>> completeMap = new HashMap<String, List<List<Double>>>();
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(bruteFile));
			String line =  null;
			
			while((line=br.readLine())!=null){
		    	
		    		if(!line.startsWith("#")) {
		    			String str[] = line.split(",");
			        String key = str[0]+"/"+str[1]+"/"+str[2];
			        
			        List <Double> temp = new ArrayList<Double>();
			        	temp.add(Double.parseDouble(str[3])); 	// Duration
			        	temp.add(Double.parseDouble(str[4]));	// Total request
			        	temp.add(Double.parseDouble(str[5]));	// Success request
			        	temp.add(Double.parseDouble(str[6]));	// Success request percent
			        	
			        	temp.add(Double.parseDouble(str[7]));	// Sucesso Variancia - Valor menos media total
			        	temp.add(Double.parseDouble(str[8]));  	// Sucesso variancia ao quadrado
			        	
			        	temp.add(Double.parseDouble(str[9]));  	// Duration variancia ao quadrado
			        	temp.add(Double.parseDouble(str[10]));  	// Duration variancia ao quadrado
			        	
			        List<List<Double>> tempList = completeMap.get(key);
			        if(tempList == null) {	
			        		tempList = new ArrayList<List<Double>>();
			        }
			        tempList.add(temp);
			        
			        completeMap.put(key, tempList);
		    		}
		        
		    }
			
			if(br!=null) {
				br.close();
			}
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/** Calculating: Read complete map and save to processed map (average) */
		HashMap<String,List<Double>> processedMap = new HashMap<String, List<Double>>();
		
		for (String completeMapkey : completeMap.keySet()) {
			
			double durationSum = 0;
			double totalSum = 0;
			double successSum = 0;
			double percentSum = 0;
			double successVarianceSquareSum = 0;
			double durationVarianceSquareSum = 0;
			
			int simulationCount = 0;
			for (List<Double> element : completeMap.get(completeMapkey)) {				
				
				// System.out.println(element);
				
				durationSum += element.get(0);
				totalSum += element.get(1);
				successSum += element.get(2);
				percentSum += element.get(3);		
				
				successVarianceSquareSum += element.get(5);
				durationVarianceSquareSum += element.get(7);
				
				simulationCount++;
				
			}
			
			List<Double> processedIndividual =  new ArrayList<Double>();
			processedIndividual.add(0,durationSum/simulationCount);	// Media
			processedIndividual.add(1,totalSum/simulationCount);		// Media
			processedIndividual.add(2,successSum/simulationCount);	// Media
			double mediaPercentSuccess = percentSum/simulationCount;
			processedIndividual.add(3,mediaPercentSuccess); 	// Media
			
			/** ################## Desvio padrao do Sucesso ################### */
			// System.out.println(completeMapkey+" "+varianciaAoQuadradoSum + " " + (Math.sqrt( varianciaAoQuadradoSum/(simulationCount/1))) ) ;			
			double desvioPadrao = Math.sqrt( successVarianceSquareSum/(simulationCount-1) ); // Desvio padrao
			desvioPadrao = desvioPadrao*1.96;
			processedIndividual.add(4,desvioPadrao);
			
			/** ################## Desvio padrao da Duracao ################### */
			desvioPadrao = Math.sqrt( durationVarianceSquareSum/(simulationCount-1) ); // Desvio padrao
			desvioPadrao = desvioPadrao*1.96;
			processedIndividual.add(5,desvioPadrao);
			
			processedMap.put(completeMapkey, processedIndividual);
			
		}
		
		//for (String processedMapKey : processedMap.keySet()) {
		//	System.out.println(processedMap.get(processedMapKey));
		//}
		
		
		/** Keys map */
		List<String> stringKeys = new ArrayList(processedMap.keySet());
		// System.out.println(stringKeys);
		Map<Integer, String> lineMapOrderedTemp = new TreeMap<>();
		for (String key : stringKeys) {
			String[] parts = key.split("/");
			String usersString = parts[0];
			int usersInterger = Integer.parseInt(usersString);				
			lineMapOrderedTemp.put(usersInterger, (parts[0]+"/"+parts[1]) );
		}
		//System.out.println(lineMapOrderedTemp);
		
		/** Read processed map and save to 2 ordered lists (container and vm). */		
		
		Map<Integer,List<Double>> processedContainerMap = new TreeMap<>();
		Map<Integer,List<Double>> processedVmMap = new TreeMap<>();
		
		for (String processedMapKey : processedMap.keySet()) {
			
			String str[] = processedMapKey.split("/");
			String machineType = str[2];
			String numberOfUsers = str[0];
			
			if(machineType.equals("container")) {				
				processedContainerMap.put(Integer.parseInt(numberOfUsers), processedMap.get(processedMapKey));
				
			}else if(machineType.equals("vm")) {
				processedVmMap.put(Integer.parseInt(numberOfUsers), processedMap.get(processedMapKey));
				
			}
		}
		
		//System.out.println(processedContainerMap);
		//System.out.println(processedVmMap);
		
		/** Save 2 ordered lists to file */			
				
		try {
			
			String header = "# simulation, duration, total-requests, success-requests, success-perc, success-dev, duration-dev";
			
			// Creating CSV notes
			System.out.println(stringKeys);
			LinkedHashSet<Integer> contUsers = new LinkedHashSet<>();
			LinkedHashSet<Integer> vmUsers = new LinkedHashSet<>();
			String contRamp = "";
			String vmRamp = "";
			for(String k: stringKeys) {
				String kArray[] = k.split("/");
				if(kArray[2].equals("container")) {
					contUsers.add(Integer.parseInt(kArray[0]));
					contRamp = kArray[1];
				}else if(kArray[2].equals("vm")) {
					vmUsers.add(Integer.parseInt(kArray[0]));
					vmRamp = kArray[1];
				}
			}
			
			TreeSet<Integer> contUsersTreeSet = new TreeSet<Integer>();
			TreeSet<Integer> vmUsersTreeSet = new TreeSet<Integer>();
			contUsersTreeSet.addAll(contUsers);
			vmUsersTreeSet.addAll(contUsers);
			
			String contNotes = "# ";
			contNotes += "Container - ramp:"+contRamp+" users:";
			int j=0;
			for(Integer val: contUsersTreeSet) {
				contNotes += val;
				if(j<contUsersTreeSet.size()-1) {
					contNotes+=",";
				}
				j++;
			}
			
			String vmNotes = "# ";
			vmNotes += "Vm - ramp:"+vmRamp+" users:";
			j=0;
			for(Integer val: vmUsersTreeSet) {
				vmNotes += val;
				if(j<vmUsersTreeSet.size()-1) {
					vmNotes+=",";
				}
				j++;
			}
			
			// Container
			PrintWriter writerContainer = null;
			writerContainer = new PrintWriter(processedContainerFile, "UTF-8");
			writerContainer.println(contNotes);
			writerContainer.println("# File: "+processedContainerFile);
			writerContainer.println(header);			
			
			for (Integer key : processedContainerMap.keySet()) {
				
				writerContainer.print(lineMapOrderedTemp.get(key)+",");
				
				int i = 0;
				for(Double value: processedContainerMap.get(key)) {
					writerContainer.print(value);
					// the last 
					if(i < processedContainerMap.get(key).size()-1) {
						writerContainer.print(",");
					}else {
						writerContainer.println();
					}	
					i++;					
				}
			}
			
			// VM
			PrintWriter writerVm = null;
			writerVm = new PrintWriter(processedVmFile, "UTF-8");
			writerVm.println(vmNotes);
			writerVm.println("# "+processedVmFile);
			writerVm.println(header);	
			
			for (Integer key : processedVmMap.keySet()) {
				
				writerVm.print(lineMapOrderedTemp.get(key)+",");
				
				int i = 0;
				for(Double value: processedVmMap.get(key)) {
					writerVm.print(value);
					// the last 
					if(i < processedVmMap.get(key).size()-1) {
						writerVm.print(",");
					}else {
						writerVm.println();
					}	
					i++;					
				}
			}
			
			if(writerContainer!=null) {
				writerContainer.close();
			}
			
			if(writerVm!=null) {
				writerVm.close();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		

		
		
	}
	
}
