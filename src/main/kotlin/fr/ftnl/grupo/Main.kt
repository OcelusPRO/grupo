package fr.ftnl.grupo

import com.google.gson.Gson
import com.google.gson.GsonBuilder


val GSON: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()



fun main(args : Array<String>) {
	println("Hello World!")
	
	// Try adding program arguments via Run/Debug configuration.
	// Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
	println("Program arguments: ${args.joinToString()}")
}