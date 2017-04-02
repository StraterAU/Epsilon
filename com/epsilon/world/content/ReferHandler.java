package com.epsilon.world.content;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ReferHandler {

	public static List<String> SERIALS = new ArrayList<>();
	
	public static void add(String serial) {
		SERIALS.add(serial);
		try {
			save();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static boolean contains(String serial) {
		return SERIALS.contains(serial);
	}
	
	public static void load() {
		
		try {
		
			Path path = Paths.get("./data/refers.json");
			File file = path.toFile();
			FileReader fileReader = new FileReader(file);
			JsonParser fileParser = new JsonParser();
			Gson builder = new GsonBuilder().create();
			JsonObject reader = (JsonObject) fileParser.parse(fileReader);
			
			SERIALS = builder.fromJson(reader.get("serials"), SERIALS.getClass());
		
		} catch(Throwable t ){
			t.printStackTrace();
		}
		
	}
	
	public static void save() throws Throwable {
		
		Path path = Paths.get("./data/refers.json");
		File file = path.toFile();
		
		if(!file.exists()) {
			file.createNewFile();
		}
		
		file.getParentFile().setWritable(true);
		
		Gson builder = new GsonBuilder().setPrettyPrinting().create();
		JsonObject object = new JsonObject();
		FileWriter writer = new FileWriter(file);
		
		object.add("serials", builder.toJsonTree(SERIALS));
		
		writer.write(builder.toJson(object));
		writer.close();
		
	}
	
}
