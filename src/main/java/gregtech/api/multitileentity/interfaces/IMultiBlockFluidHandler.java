package gregtech.api.multitileentity.interfaces;

import gregtech.api.multitileentity.multiblock.base.MultiBlockPart;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public interface IMultiBlockFluidHandler {
    int fill                     (MultiBlockPart aPart, ForgeDirection aDirection, FluidStack aFluid, boolean aDoFill);
    FluidStack drain             (MultiBlockPart aPart, ForgeDirection aDirection, FluidStack aFluid, boolean aDoDrain);
    FluidStack drain             (MultiBlockPart aPart, ForgeDirection aDirection, int aAmountToDrain, boolean aDoDrain);
    boolean canFill              (MultiBlockPart aPart, ForgeDirection aDirection, Fluid aFluid);
    boolean canDrain             (MultiBlockPart aPart, ForgeDirection aDirection, Fluid aFluid);
    FluidTankInfo[] getTankInfo  (MultiBlockPart aPart, ForgeDirection aDirection);
}
