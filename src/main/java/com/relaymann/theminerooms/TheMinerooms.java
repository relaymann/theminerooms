package com.relaymann.theminerooms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.neoforged.fml.common.Mod;
/** Main entry point; construction is safe for dedicated servers. */
@Mod(TheMinerooms.MOD_ID)
public final class TheMinerooms {
 public static final String MOD_ID = "theminerooms";
 public static final Logger LOGGER = LoggerFactory.getLogger("The Minerooms");
 public TheMinerooms() { LOGGER.info("The Minerooms foundation is loading."); }
}
