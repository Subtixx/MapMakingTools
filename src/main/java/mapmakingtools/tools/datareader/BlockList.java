package mapmakingtools.tools.datareader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.NumberParse;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class BlockList {
	
	public static List<ItemStack> stacksDisplay = new ArrayList<ItemStack>();
	
	public static Iterator<ItemStack> getIterator() {
		return stacksDisplay.iterator();
	}
	
	public static List<ItemStack> getList() {
		return stacksDisplay;
	}
	
	public static int getListSize() {
		return stacksDisplay.size();
	}
	
	public static void addStack(String blockName, int blockMeta) {
		Block block = Block.getBlockFromName(blockName);
		if(block != null) {
			ItemStack res = new ItemStack(block, 1, blockMeta);
			stacksDisplay.add(res);
		}else{
			FMLLog.severe("Block "+blockName+" not found in registry");
		}
	}
	
	public static void readDataFromFile() {
		try {
			BufferedReader paramReader = new BufferedReader(new InputStreamReader(MapMakingTools.class.getResourceAsStream("/assets/mapmakingtools/data/blocks.txt"))); 
			String line = "";
			while((line = paramReader.readLine()) != null) {
				
				if(line.isEmpty() || line.startsWith("#"))
					continue;
				
				String[] dataParts = line.split(" ~~~ ");
				if(dataParts.length != 2)
					continue;
					
				if(!NumberParse.isInteger(dataParts[1]))
					continue;
					
				String block = dataParts[0];
				int meta = NumberParse.getInteger(dataParts[1]);
				
				addStack(block, meta);
			}
	    }
		catch(Exception e) {
			e.printStackTrace();
	    }
	}
}
