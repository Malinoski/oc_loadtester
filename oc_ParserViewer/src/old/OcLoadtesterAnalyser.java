package old;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OcLoadtesterAnalyser {

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void old(String ars[]) {
		
		List<Object> resultList = new ArrayList<>();
		List<Object> header = new ArrayList<>();
		header.add("Users/Seconds");
		
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
							System.out.println("-----------------------------------------------");
							System.out.print("Simulation:"+rampPath+"\n\n");
							
							String[] parts = rampPath.toString().split("/");
							List<Object> line = new ArrayList<>();
							line.add(parts[parts.length-2]+"/"+parts[parts.length-1]); // To get ramp0010 (size-2 index) user500 (size-1 index)
							line.add(1, "" );
							line.add(2, "" );							
							
							machineFile = new File(rampPath.getAbsolutePath());
							machinePaths = machineFile.listFiles();
							
							for (File machinePath : machinePaths) {

								List<List<Integer>> results = new ArrayList<>();
								
								// Print 
								System.out.println("Machine:"+machinePath.getName());								
								
								File simulationFile = null;
								File[] sinulationPaths;
								try {									

									simulationFile = new File(machinePath.getAbsolutePath());
									sinulationPaths = simulationFile.listFiles();
									for (File simulationPath : sinulationPaths) {

//										System.out.println(simulationPath);
										// System.out.println(resultPath.getAbsolutePath());
										//System.out.println("Simulation:\t"+simulationPath);

										// TOTAL (duration)
										String line40 = Files.readAllLines(Paths.get(simulationPath.getAbsoluteFile() + "/index.html")).get(40);
										List<String> myList = new ArrayList<String>(Arrays.asList(line40.split(" ")));
										//System.out.println(myList.get(33));
										String duration = myList.get(33);
										
										//totalArray.add(Integer.parseInt(duration));

										// GLOBAL
										String jsonContent = readFile(simulationPath + "/js/global_stats.json",StandardCharsets.UTF_8);
										Map<String, Object> map = new Gson().fromJson(jsonContent, new TypeToken<Map<String, Object>>() {}.getType());
										
										// numberOfRequests
										Map<String, Object> numberOfRequests = (Map<String, Object>)map.get("numberOfRequests");										
										// KO
										Double ko = (double)numberOfRequests.get("ko"); 
										//failedArray.add(ko.intValue());										
										// OK
										Double ok = (double)numberOfRequests.get("ok");
										//successArray.add(ok.intValue());										
										// TOTAL (numberOfRequests)
										Double totalRequest = (double)numberOfRequests.get("total");
										//totalRequestArray.add(totalRequest.intValue());
										
										List<Integer> result = new ArrayList<>();
										result.add(Integer.parseInt(duration));
										result.add(totalRequest.intValue());
										result.add(ok.intValue());
										result.add(ko.intValue());
										
										results.add(result);

									}
									
									double avSum = 0;
									int avTotal = results.size();
									
									//////////////// Durations
									System.out.print("Durations: \t");
									for (List<Integer> list : results) {
										avSum += list.get(0);
										System.out.print(list.get(0)+"\t");
									}
									System.out.printf("\nDuration (av):\t%.2f\n",avSum/avTotal);
									
									//////////////// Requests
									System.out.print("Requests: \t");
									for (List<Integer> list : results) {
										System.out.print(list.get(1)+"\t");
									}
									System.out.println();
									
									///////////////// Success
									System.out.print("Success (%): \t");
									avSum = 0;
									for (List<Integer> list : results) {
										double total = list.get(1);
										double ok = list.get(2);
										double result = (ok*100)/total;
										avSum += result;
										if(result%1==0) {
											System.out.printf("%.0f%%\t",result);											
										}else {
											System.out.printf("%.2f%%\t",result);
										}
									}
									System.out.print("\nSuccess (n): \t");
									for (List<Integer> list : results) {
										System.out.printf("%d\t",list.get(2));
									}
									System.out.printf("\nSuccess (av)\t%.2f%%\n",avSum/avTotal);
									
									if(machinePath.getName().contains("ontainer")) {
										line.add(1, String.valueOf( (avSum/avTotal)/100) );
									}else {
										line.add(2,String.valueOf( (avSum/avTotal)/100) );
									}								
									
									/////////////////// Failed
									System.out.print("Failed (%): \t");
									avSum = 0;
									for (List<Integer> list : results) {
										double total = list.get(1);
										double ko = list.get(3);
										double result = (ko*100)/total;
										avSum += result;
										if(result%1==0) {
											System.out.printf("%.0f%%\t",result);											
										}else {
											System.out.printf("%.2f%%\t",result);
										}
										
									}
									System.out.print("\nFailed (n): \t");
									for (List<Integer> list : results) {
										System.out.printf("%d\t",list.get(3));
									}
									System.out.printf("\nFailed (av)\t%.2f%%\n",avSum/avTotal);
									System.out.println();System.out.println();
									
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
							
							//resultList.add(line);
							
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
		// Result file to create eand fill (created from gatling result files)
		PrintWriter writer = null;
		try {
			
			writer = new PrintWriter(finalFile, "UTF-8");		

			resultList.add(header);
			
			for (int i = 0; i < resultList.size(); i++) {				
				List<Object> a = (List<Object>) resultList.get(i);
				
				String firstColumn = "";
				if(a.size()>0) {
					firstColumn = (String)a.get(0);
				}
				
				String secondColumn = "";
				if(a.size()>1) {
					secondColumn = (String)a.get(1);
				}
				
				String thirdColumn = "";
				if(a.size()>2) {
					thirdColumn = (String)a.get(2);
				}
				
				writer.println(firstColumn+","+secondColumn+","+thirdColumn);
			}
		
		
		} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		}
		
		if(writer!=null) {
			writer.close();
		}
			
		
	}
}
