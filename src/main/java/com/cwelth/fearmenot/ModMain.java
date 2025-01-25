package com.cwelth.fearmenot;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.common.ForgeConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Mod(ModMain.MODID)
@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
// ModMain.java
public class ModMain {
    public static final String MODID = "fearmenot"; // ID do seu mod
    public static final Logger LOGGER = LogManager.getLogger();

    // Construtor do mod
    public ModMain() {
        // Inicializa a configuração
        Configuration.init();

        // Registra o contexto de configuração
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);

        // Registra o evento de setup
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Verifica se o arquivo de configuração existe e, se não, cria ele com valores padrão
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("fearmenot-common.toml");
        if (Files.notExists(configPath)) {
            LOGGER.info("Config file not found, creating default config...");
            try {
                // Cria o arquivo com valores padrão
                Files.createDirectories(configPath.getParent()); // Cria o diretório, se necessário
                Files.copy(getClass().getResourceAsStream("/assets/fearmenot/fearmenot-common.toml"), configPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                LOGGER.error("Failed to create default config file", e);
            }
        }

        // Carregar a configuração corretamente
        Configuration.loadConfig(Configuration.COMMON_CONFIG, configPath);
        LOGGER.info("Mod setup complete!");
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // Este é o momento em que o servidor começa
        LOGGER.info("Server starting...");
    }

    // Classe interna para lidar com as configurações
    public static class Configuration {
        // Definir as configurações aqui
        public static ForgeConfigSpec.BooleanValue MOD_ENABLED;
        public static ForgeConfigSpec COMMON_CONFIG;
        public static ForgeConfigSpec.IntValue NEAR_SPEED;
        public static ForgeConfigSpec.DoubleValue FAR_SPEED;
        public static ForgeConfigSpec.DoubleValue AVOID_DISTANCE;
        public static ForgeConfigSpec.IntValue CROUCH_DISTANCE;

        public static ForgeConfigSpec.Builder builder;

        // Inicializar configurações
        public static void init() {
            builder = new ForgeConfigSpec.Builder();

            MOD_ENABLED = builder
                    .comment("Ativar/desativar o mod")
                    .define("modEnabled", true);  // true se o mod estiver ativado por padrão

            // Definir variáveis de configuração
            NEAR_SPEED = builder
                    .comment("Velocidade perto")
                    .defineInRange("nearSpeed", 5, 1, 10);   // Velocidade perto (valor entre 1 e 10)

            FAR_SPEED = builder
                    .comment("Velocidade longe")
                    .defineInRange("farSpeed", 1.0, 0.1, 5.0);    // Velocidade longe (valor entre 0.1 e 5)

            AVOID_DISTANCE = builder
                    .comment("Distância de evitação")
                    .defineInRange("avoidDistance", 15.0, 0.0, 20.0);  // Distância de evitação (valor entre 0 e 20)

            CROUCH_DISTANCE = builder
                    .comment("Distância de agachar")
                    .defineInRange("crouchDistance", 3, 0, 10);  // Distância de agachar (valor entre 0 e 10)

            // Cria o objeto de configuração
            COMMON_CONFIG = builder.build();
        }

        // Carregar a configuração
        public static void loadConfig(ForgeConfigSpec spec, Path path) {
            final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();

            configData.load();
            spec.setConfig(configData);  // Configuração carregada
        }
    }
}

