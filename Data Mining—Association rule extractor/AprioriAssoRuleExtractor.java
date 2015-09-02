import java.io.*;
import java.util.*;

class AprioriAssoRuleExtractor{

	static ArrayList<ArrayList<Item>> allRows = new ArrayList<ArrayList<Item>>(); // Contains all rows in file
	static ArrayList<ArrayList<ItemWithSup>> allLargeSets = new ArrayList<ArrayList<ItemWithSup>>(); // Contains all large item sets
	static ArrayList<RuleList> ruleList = new ArrayList<RuleList>(); // Contains all high-confidence association rules
	static int totalNumberOfRows=0;
	static double min_sup=0.0;
	static double min_conf=0.0;

	//Item
	static class Item{
		ArrayList<String> value;
		ArrayList<Integer> colNum;
		public Item(String val,int colN){
			value = new ArrayList<String>();
			colNum = new ArrayList<Integer>();
			value.add(val);
			colNum.add(colN);
		}
		public void InsertNewItem(String val,int colN){
			value.add(val);
			colNum.add(colN);
		}
	}
	//Item with corresponding support
	static class ItemWithSup{
		Item item;
		double sup;
		public ItemWithSup(Item item,double sup){
			this.item=item;
			this.sup=sup;
		}
		public void IncreaseSup(double val){
			sup += val;
		}
	}
	//Rule
	static class Rule{
		ArrayList<String> lhs;
		String rhs;
		public Rule(ArrayList<String> lhs,String rhs){
			this.lhs = new ArrayList<String>();
			this.lhs = lhs;
			this.rhs = rhs;
		}
	}
	//Rule list
	static class RuleList{
		ArrayList<String> lhs;
		String rhs;
		double conf;
		double sup;
		public RuleList(ArrayList<String> lhs,String rhs,double conf,double sup){
			this.lhs = new ArrayList<String>();
			this.lhs = lhs;
			this.rhs = rhs;
			this.conf = conf;
			this.sup = sup;
		}
	}

	// Calculate first large itemset L_1
	public static void CalculateL_1(String fileName){
		Hashtable<Item,Integer> L1_table = new Hashtable<Item,Integer>(); // Holds each [value,colNumber]'s occurance
		ArrayList<ItemWithSup> L1List = new ArrayList<ItemWithSup>();  // Holds [value, colNumber, sup] entries where sup>=min_sup
		BufferedReader br = null;
		String line = "";
		int rowCount = 0;

		try {
			br = new BufferedReader(new FileReader(fileName));

			while((line=br.readLine())!=null){
				rowCount++;
				String[] lineParse=line.split(",");
				ArrayList<Item> row = new ArrayList<Item>();
				// Traverse each item in each line, record its appearance time together with its column number information
				for(int i=0;i<lineParse.length;i++){
					String attribute = lineParse[i];
					// if attribute is NULL, skip
					if(attribute.length()==0){
						continue;
					}else{
						Item item = new Item(attribute,i);  // save as [attr,colNum] 
						boolean found = false;
						for(Item key:L1_table.keySet()){
							if(((key.value.get(0)).equals(item.value.get(0)))&&((key.colNum.get(0))==(item.colNum.get(0)))){
								L1_table.put(key,L1_table.get(key)+1);
								found=true;
								break;
							}
						}
						if(!found){
							L1_table.put(item,1);
						}
						row.add(item);
					}
				}//end of for loop

				if(row.size()>0){
					allRows.add(row); //Save all rows
				}
			}// end of while
			
			// Pick item that has higher support than min_sup, add to L1List
			for(Item key:L1_table.keySet()){
				double value = L1_table.get(key)/(double)rowCount;
				if(value >= min_sup){
					Item tmpItem = key;
					L1List.add(new ItemWithSup(tmpItem, value));
				}
			}

			totalNumberOfRows = rowCount;
			allLargeSets.add(L1List); // Store to global large itemset set
			br.close();
		}catch(Exception e){e.printStackTrace();}
	}

	// Derive candidates from L_k-1
	public static ArrayList<ItemWithSup> RetrieveCandidates(ArrayList<ItemWithSup> Lprevious){
		ArrayList<Item> tempL = new ArrayList<Item>();
		for(ItemWithSup i:Lprevious){
			tempL.add(i.item);
		}

		ArrayList<Item> L_joinSet = new ArrayList<Item>(); // Holds all joined results

		//Pick two itemsets in L_list
		for(int i=0;i<tempL.size();i++){
			int k = tempL.get(i).value.size() +1; 
			for (int j=i+1; j<tempL.size();j++){
				boolean joinable = true;
				//compare first (k-2) items in L_list[i] & L_list[j]
				for (int t = 0; t< k-2;t++){
					Item item1 = new Item(tempL.get(i).value.get(t),tempL.get(i).colNum.get(t));
					Item item2 = new Item(tempL.get(j).value.get(t),tempL.get(j).colNum.get(t));
					if (!(item1.value.get(0).equals(item2.value.get(0))) || (item1.colNum.get(0)!=item2.colNum.get(0))){
						joinable=false;
						break;
					}
				}

				//Check last item, if equal, not joinable
				if(joinable){
					Item item1 = new Item(tempL.get(i).value.get(k-2),tempL.get(i).colNum.get(k-2));
					Item item2 = new Item(tempL.get(j).value.get(k-2),tempL.get(j).colNum.get(k-2)); 
					if(item1.colNum.get(0)==item2.colNum.get(0)){
						joinable = false;
					}
				}

				// If joinable, insert item in the order of their column numbers
				if(joinable){
					ArrayList<Item> joined_list = new ArrayList<Item>();
					for(int l=0;l<k-2;l++){
						Item item = new Item(tempL.get(i).value.get(l),tempL.get(i).colNum.get(l));
						joined_list.add(item);
					}
					Item item1 = new Item(tempL.get(i).value.get(k-2),tempL.get(i).colNum.get(k-2));
					Item item2 = new Item(tempL.get(j).value.get(k-2),tempL.get(j).colNum.get(k-2)); 
					// Sort by column number
					if(item1.colNum.get(0)<item2.colNum.get(0)){
						joined_list.add(item1);
						joined_list.add(item2);
					}else{
						joined_list.add(item2);
						joined_list.add(item1);
					}
					//Create item to be added into L_joinSet
					int joined_list_len = joined_list.size();
					Item joinedItem = new Item(joined_list.get(0).value.get(0),joined_list.get(0).colNum.get(0));
					for(int m=1;m<joined_list_len;m++){
						joinedItem.InsertNewItem(joined_list.get(m).value.get(0),joined_list.get(m).colNum.get(0));
					}
					//ADD to L_joinSet
					L_joinSet.add(joinedItem);
				}
			}
		}

		//Prune
		ArrayList<ItemWithSup> ret = new ArrayList<ItemWithSup>();
		//Check if any (k-1) subset is in L_{k-1}
		for(Item item: L_joinSet){
			Boolean valid = true;
			for(int i=0;i<item.value.size();i++){
				ArrayList<String> attr = new ArrayList<String>(); 
				int j = 0;
				while(j<item.value.size()){
					if(j==i){
						j++;
						continue;
					}
					else{
						attr.add(item.value.get(j));
						j++;
					}
				}

				for(String s: attr){
					Boolean found = false;
					for(Item it: tempL){
						if(it.value.get(0).equals(s)){
							found = true;
							break;
						}
					}
					if(!found){
						valid = false;
						break;
					}
				}
			}// End of For loop
			//If valid, add to return list with sup initialized to 0.0
			if(valid){
				ret.add(new ItemWithSup(item,0.0));
			}
		}
		return ret;
	}

	public static void ExtractLargeItemsets(String fileName){
		int largeSetNo = 1;
		ArrayList<ItemWithSup> Lprevious = allLargeSets.get(largeSetNo-1);
		// If L_k-1 has no item, stop extracting large itemsets
		while(Lprevious.size()!=0){
			//get candidate C_k from L_k-1 (including prune step)
			ArrayList<ItemWithSup> C_k = RetrieveCandidates(Lprevious);
			ArrayList<ItemWithSup> L_k = new ArrayList<ItemWithSup>();
			// compute the support of each candidate
			for(ArrayList<Item> row:allRows){
				//Compute score for each candidate
				for(int i=0;i<C_k.size();i++){
					ItemWithSup tmp = C_k.get(i);
					Boolean contained = true;
					for(String s:tmp.item.value){
						Boolean found =false;
						for(Item item:row){
							if(item.value.get(0).equals(s)){
								found = true;
							}
						}
						if(!found){
							contained=false;
							break;
						}
					}
					// if the row contains candidate, increase support value by 1 for that candidate
					if(contained){
						C_k.get(i).IncreaseSup(1.0);
					}
				}
			}

			// Add only large sets that have sup > min_sup
			for(ItemWithSup iws: C_k){
				iws.sup=(iws.sup)/(double)totalNumberOfRows;
				if(iws.sup>=min_sup){
					L_k.add(iws);
				}
			}

			allLargeSets.add(L_k);
			Lprevious = L_k;
			largeSetNo++;
		}
	}

	// Extract rules and print out
	public static void ExtractRules(){
		int startIndex=1;
		while(startIndex < allLargeSets.size()){
			ArrayList<ItemWithSup> L_k = allLargeSets.get(startIndex);
			for(ItemWithSup iws : L_k){
				// Generate rules for current Large itemset 
				ArrayList<Rule> rules = GetRules(iws.item.value);
				for(Rule rule: rules){
					ArrayList<String> lhs = new ArrayList<String>();
					lhs=rule.lhs;
					String rhs=rule.rhs;
					// Confidence == Support(LHS U RHS)/Support(LHS)
					double conf = iws.sup / GetSupport(lhs);
					// Add only rules which have higher conf than min_conf, record all such rules
					if(conf>=min_conf){
						ruleList.add(new RuleList(lhs,rhs,conf,iws.sup));
					}
				}
			}
			startIndex++;
		}
	}

	// Get Support of LHS
	public static double GetSupport(ArrayList<String> lhs){
		int size = lhs.size();
		ArrayList<ItemWithSup> L_k = allLargeSets.get(size-1);
		ArrayList<String> arr = new ArrayList<String>();

		for(ItemWithSup iws:L_k){
			Boolean included = true;
			arr = new ArrayList<String>();
			for(String s:iws.item.value){
				arr.add(s);
			}
			for(int i=0;i<lhs.size();i++){
				String item = lhs.get(i);
				if (!arr.contains(item)){
					included = false;
					break;
				}
				arr.remove(item);
			}
			if(included){
				return iws.sup;
			}
		}
		return Double.MAX_VALUE;
	}


	// Generate all possible rules: one item on rhs,>=1 items on lhs, intersection of lhs and rhs is empty
	public static ArrayList<Rule> GetRules(ArrayList<String> itemsets){
		ArrayList<Rule> rules = new ArrayList<Rule>();
		for(int i=0;i<itemsets.size();i++){
			ArrayList<String> lhs = new ArrayList<String>();
			String rhs = itemsets.get(i);
			int j = 0;
			while(j<itemsets.size()){
				if(j==i){
					j++;
					continue;
				}
				else{
					lhs.add(itemsets.get(j));
					j++;
				}
			}
			Rule rule = new Rule(lhs,rhs);
			rules.add(rule);
		}
		return rules;
	}

	// Write results
	public static void WriteResults(){
		File f = new File("output.txt");
		f.delete();
		try{
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.txt", true)));
		    int largeItemCount=0;
			for(int i=0;i<allLargeSets.size();i++){
				for(int j=0;j<allLargeSets.get(i).size();j++){
					largeItemCount++;
				}
			}
			out.println("==Frequent itemsets (min_sup="+min_sup*100+"%)");
			System.out.println("We have "+largeItemCount+" large itemsets");
			// Print out large itemset with decreasing order of support
			while(largeItemCount>0){
				double max_sup = 0.0;
				String max_sup_item = null;
				int max_i=0;
				int max_j=0;
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<allLargeSets.size();i++){
					ArrayList<ItemWithSup> tmpArr = new ArrayList<ItemWithSup>();
					tmpArr = allLargeSets.get(i);
					if(tmpArr.size()==0){
						continue;
					}
					else{
						for(int j=0;j<tmpArr.size();j++){
							if(tmpArr.get(j).sup>max_sup){
								max_sup=tmpArr.get(j).sup;
								sb = new StringBuffer();
								for(int k=0;k<tmpArr.get(j).item.value.size();k++){
									if(k!=tmpArr.get(j).item.value.size()-1){
										sb.append(tmpArr.get(j).item.value.get(k)+",");
									}
									else{
										sb.append(tmpArr.get(j).item.value.get(k));
									}
								}
								max_sup_item=sb.toString();
								max_i=i;
								max_j=j;
							}
						}
					}
				}
				allLargeSets.get(max_i).remove(max_j);
				out.println("["+max_sup_item+"]"+","+max_sup*100+"%");
				largeItemCount--;
			}

			out.println();out.println();out.println();out.println();

			// Print out rules with decreasing order of support
			out.println("==High-confidence association rules (min_conf="+min_conf*100+"%)");
			int ruleNumber = ruleList.size();
			System.out.println("We have "+ruleNumber+" rules");
			while(ruleNumber >0){
				double max_conf=0.0;
				int max_i = 0;
				double max_sup=0.0;
				ArrayList<String> max_lhs=new ArrayList<String>();
				String max_rhs=null;
				for(int i=0;i<ruleList.size();i++){
					if(ruleList.get(i).conf>max_conf){
						max_conf=ruleList.get(i).conf;
						max_i = i;
						max_lhs=ruleList.get(i).lhs;
						max_rhs=ruleList.get(i).rhs;
						max_sup=ruleList.get(i).sup;
					}
				}
				out.print("[");
				for(int i=0;i<max_lhs.size();i++){
					if(i==max_lhs.size()-1){
						out.print(max_lhs.get(i)+"]");
					}else{
						out.print(max_lhs.get(i)+",");
					}
				}
				out.print(" => ");
				out.print("["+max_rhs+"]"); 
				out.print(" (Conf: "+ (max_conf*100) + "%, Supp: "+(max_sup*100) + "%)");
				out.println();
				ruleList.remove(max_i);
				ruleNumber--;
			}
			out.close();
		}catch (IOException e) {}
		System.out.println("Done!");
	}


	public static void main(String[] args){
		if(args.length ==0){
			Scanner input = new Scanner(System.in);
			System.out.println("Enter File Name: ");
			String fName = input.nextLine();
			while(!fName.equals("INTEGRATED-DATASET")){
				System.out.println("Only support INTEGRATED-DATASET.csv file");
				System.out.println("Enter File Name: ");
				fName = input.nextLine();
			}
			System.out.println("Enter minimum support: (recommended value: 0.02 - 0.035)");
			String minSup =input.nextLine();
			double mSup = Double.parseDouble(minSup);
			while(mSup<0.0 || mSup>1.0){
				System.out.println("Only support min_sup in range (0.0, 1.0)");
				System.out.println("Enter minimum support: (recommended value: 0.02 - 0.035)");
				minSup =input.nextLine();
				mSup = Double.parseDouble(minSup);
			}
			System.out.println("Enter minimum confidence: (recommended value: 0.3 - 0.4)");
			String minConf =input.nextLine();
			double mConf = Double.parseDouble(minConf);
			while(mConf<0.0 || mConf>1.0){
				System.out.println("Only support min_conf in range (0.0, 1.0)");
				System.out.println("Enter minimum confidence: (recommended value: 0.3 - 0.4)");
				minConf =input.nextLine();
				mConf = Double.parseDouble(minConf);
			}
			String file_name = fName + ".csv";
			min_sup = mSup;
			min_conf = mConf;
			//Compute L_1
			CalculateL_1(file_name);
			//Extract large itemsets
			ExtractLargeItemsets(file_name);
			//Extract rules from large itemsets
			ExtractRules();
			WriteResults();
		}

		else{
			if(args.length<2){
				System.out.println("Usage: java AprioriAssoRuleExtractor {min_sup} {min_conf}");
				System.exit(0);
			}
			min_sup=Double.parseDouble(args[0]);
			min_conf=Double.parseDouble(args[1]);
			//Compute L_1
			CalculateL_1("INTEGRATED-DATASET.csv");
			//Extract large itemsets
			ExtractLargeItemsets("INTEGRATED-DATASET.csv");
			//Extract rules from large itemsets
			ExtractRules();
			WriteResults();
		}
	}

}