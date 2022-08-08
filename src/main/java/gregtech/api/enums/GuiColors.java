package gregtech.api.enums;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import gregtech.api.util.GT_Log;
import gregtech.GT_Mod;

public enum GuiColors {	
	//RGB Colors: Name and default value
	oneByOne 						(0x404040),
	twoByTwo 						(0x404040),
	twoByTwoFluid 					(0x404040),
	twoByTwoFluidInventory 			(0x404040),
	threeByThree 					(0x404040),
	fourByFour						(0x404040),
	basicMachine					(0x404040),
	basicTankTitle					(0x404040),
	basicTankInventory 				(0x404040),
	basicTankLiquidAmount 			(0xFAFAFF),
	basicTankLiquidValue			(0xFAFAFF),
	maintenanceHatch				(0x404040),
	maintenanceHatchRepair			(0x404040),
	
	multiMachineTitle 				(0xFAFAFF),
	multiMachineDrillBaseText 		(0xFAFAFF),
	multiMachineIncompleteStructure (0xFAFAFF),
	multiMachineLargeTurbineText	(0xFAFAFF),
	multiMachineMaintenanceText 	(0xFAFAFF),
	multiMachineMalletRestart		(0xFAFAFF),
	multiMachineRunningPerfectly	(0xFAFAFF),
	
	outputHatchTitle				(0x404040),
	outputHatchInventory			(0x404040),
	outputHatchAmount				(0xFAFAFF),
	outputHatchValue				(0xFAFAFF),
	outputHatchFluidName			(0xFAFAFF),
	outputHatchLockedFluid			(0xFAFAFF),

	boiler							(0x404040),
	bronzeBlastFurnace				(0x404040),
	fusionReactor					(0xFF0000),
	industrialApiary				(0x404040),
	microwaveEnergyTransmitter		(0xFAFAFF),
	primitiveBlastFurnace			(0x404040),
	quantumChestTitle				(0x404040),
	quantumChestAmount				(0xFAFAFF),
	regulator						(0xFAFAFF),
	teleporter						(0xFAFAFF),
	
	fluidDisplayStackRenderer		(0xFFFFFF),

	//ARGB Colors: Name and default value
	dialogSelectItem 				(0xFF555555),
	pollutionRenderer 				(0xFFFFFFFF),
	screenText 						(0xFF222222),
	
	coverArm 						(0xFF555555),
	coverControlsWork 				(0xFF555555),
	coverConveyor 					(0xFF555555),
	coverDoesWork 					(0xFF555555),
	coverEUMeter 					(0xFF555555),
	coverFluidFilterName 			(0xFF222222),
	coverFluidFilter 				(0xFF555555),
	coverFluidRegulatorWarn 		(0xFFFF0000),
	coverFluidRegulator 			(0xFF555555),
	coverItemFilter 				(0xFF555555),
	coverItemMeter 					(0xFF555555),
	coverLiquidMeter 				(0xFF555555),
	coverMaintenance 				(0xFF555555),
	coverPlayerDetector 			(0xFF555555),
	coverPump 						(0xFF555555),
	coverRedstoneWirelessBase 		(0xFF555555),
	coverShutter 					(0xFF555555),

	NEIText 						(0xFF000000)
	;

	private final String root;
	private final int color;

	GuiColors() {
		this.root = "GT5U.gui.color";
		this.color = 0x000000;
	}

	GuiColors(final int hex) {
		this.root = "GT5U.gui.color";
		this.color = hex;
	}

	public int getColor() {
		int color = this.color;
		String hex = "";

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT && GT_Mod.jsonGuiColors != null) {
            hex = GT_Mod.jsonGuiColors.getString(this.root + "." + this.toString());
        	
        	try {	
				if (!hex.isEmpty()) {color = Integer.parseUnsignedInt(hex, 16);}
			}
			catch (final NumberFormatException e) {
				GT_Log.err.println("Couldn't format color correctly for: " + this.root + " -> " + hex);
			}
        }

		return color;
	}
}