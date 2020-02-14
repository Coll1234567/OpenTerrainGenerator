package com.pg85.otg.forge;

import java.io.File;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.pg85.otg.OTG;
import com.pg85.otg.configuration.standard.PluginStandardValues;
import com.pg85.otg.forge.generator.OTGChunkGenerator;
import com.pg85.otg.forge.generator.OTGGenSettings;
import com.pg85.otg.forge.world.OTGWorldType;
import com.pg85.otg.logging.LogMarker;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("openterraingenerator")
public class OTGPlugin
{
    public static OTGWorldType OtgWorldType;
    //public static BiomeProviderType<OTGBiomeProviderSettings, OTGBiomeProvider> OtgBiomeProviderType;
    public static ChunkGeneratorType<OTGGenSettings, OTGChunkGenerator> OtgChunkGeneratorType;
    
    public OTGPlugin()
    {
        // Create the world type. WorldType registers itself in the constructor
        OtgWorldType = new OTGWorldType();

        // Start OpenTerrainGenerator engine
        OTG.setEngine(new ForgeEngine());
               
        //OtgBiomeProviderType = new BiomeProviderType<OTGBiomeProviderSettings,OTGBiomeProvider>(OTGBiomeProvider::new, OTGBiomeProviderSettings::new);        
		//ForgeRegistries.BIOME_PROVIDER_TYPES.register(OtgBiomeProviderType);
        
        OtgChunkGeneratorType = new ChunkGeneratorType<>(OTGChunkGenerator::new, true, OTGGenSettings::new);
		//ForgeRegistries.CHUNK_GENERATOR_TYPES.register(OtgChunkGeneratorType);
        
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        OTG.log(LogMarker.INFO, "HELLO FROM PREINIT");
        OTG.log(LogMarker.INFO, "DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        // do something that can only be done on the client
    	OTG.log(LogMarker.INFO, "Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> 
        { 
        	OTG.log(LogMarker.INFO, "Hello world from the MDK"); 
        	return "Hello world";
    	});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
    	OTG.log(LogMarker.INFO, "Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
        // do something when the server starts
    	OTG.log(LogMarker.INFO, "HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
            // register a new block here
        	OTG.log(LogMarker.INFO, "HELLO from Register Block");
        }
        
        @SubscribeEvent
        public static void onBiomesRegistry(final RegistryEvent.Register<Biome> biomeRegistryEvent)
        {
    	    File OTGWorldsDirectory = new File(OTG.getEngine().getOTGRootFolder().getAbsolutePath() + File.separator + PluginStandardValues.PresetsDirectoryName);
    	    if(OTGWorldsDirectory.exists() && OTGWorldsDirectory.isDirectory())
    	    {
    	    	((ForgeEngine)OTG.getEngine()).getWorldLoader().createDefaultOTGWorld("Default"); // For MP servers, world name == preset name.
    	    	for(File worldDir : OTGWorldsDirectory.listFiles())
    	    	{
    	    		if(worldDir.isDirectory() && !worldDir.getName().toLowerCase().trim().startsWith("dim-"))
    	    		{
    	    			for(File file : worldDir.listFiles())
    	    			{
    	    				if(file.getName().equals("WorldConfig.ini"))
    	    				{
    	    					((ForgeEngine)OTG.getEngine()).getWorldLoader().registerBiomesForPreset(worldDir);
    					        break;
    	    				}
    	    			}
    	    		}
    	    	}
    		}
        }
    }   
}
