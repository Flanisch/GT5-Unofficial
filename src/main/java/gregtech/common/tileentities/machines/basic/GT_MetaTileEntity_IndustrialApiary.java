package gregtech.common.tileentities.machines.basic;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.*;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.core.*;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IIndividual;
import forestry.core.errors.EnumErrorCode;
import forestry.plugins.PluginApiculture;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_ApiaryUpgrade;
import gregtech.api.util.GT_Utility;
import gregtech.common.GT_Client;
import gregtech.common.gui.GT_Container_IndustrialApiary;
import gregtech.common.gui.GT_GUIContainer_IndustrialApiary;
import net.bdew.gendustry.api.ApiaryModifiers;
import net.bdew.gendustry.api.items.IApiaryUpgrade;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import static gregtech.api.enums.GT_Values.V;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GT_Utility.moveMultipleItemStacks;

public class GT_MetaTileEntity_IndustrialApiary extends GT_MetaTileEntity_BasicMachine implements IBeeHousing, IBeeHousingInventory, IErrorLogic, IBeeModifier, IBeeListener {

    public static final int baseEUtUsage = 37;
    static final int queen = 5;
    static final int drone = 6;


    IBeeRoot beeRoot = (IBeeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");

    public int mSpeed = 0;
    public boolean mLockedSpeed = true;

    private ItemStack usedQueen = null;
    private IBee usedQueenBee = null;
    private IEffectData[] effectData = new IEffectData[2];

    public GT_MetaTileEntity_IndustrialApiary(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 4, new String[] { "BEES GOES BRRRR", EnumChatFormatting.GRAY + "Added by " + EnumChatFormatting.GOLD + "kuba6000" }, 6, 9, "IndustrialApiary.png", "",
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_SIDE_INDUSTRIAL_APIARY_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_SIDE_INDUSTRIAL_APIARY_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_SIDE_INDUSTRIAL_APIARY),
                        TextureFactory.builder().addIcon(OVERLAY_SIDE_INDUSTRIAL_APIARY_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_FRONT_INDUSTRIAL_APIARY_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_INDUSTRIAL_APIARY_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_FRONT_INDUSTRIAL_APIARY),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_INDUSTRIAL_APIARY_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_TOP_INDUSTRIAL_APIARY_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_TOP_INDUSTRIAL_APIARY_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_TOP_INDUSTRIAL_APIARY),
                        TextureFactory.builder().addIcon(OVERLAY_TOP_INDUSTRIAL_APIARY_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_BOTTOM_INDUSTRIAL_APIARY_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_BOTTOM_INDUSTRIAL_APIARY_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_BOTTOM_INDUSTRIAL_APIARY),
                        TextureFactory.builder().addIcon(OVERLAY_BOTTOM_INDUSTRIAL_APIARY_GLOW).glow().build()));
    }

    public GT_MetaTileEntity_IndustrialApiary(String aName, int aTier, String aDescription, ITexture[][][] aTextures, String aGUIName, String aNEIName) {
        super(aName, aTier, 4, aDescription, aTextures, 6, 9, aGUIName, aNEIName);
    }

    public GT_MetaTileEntity_IndustrialApiary(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures, String aGUIName, String aNEIName) {
        super(aName, aTier, 4, aDescription, aTextures, 6, 9, aGUIName, aNEIName);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_IndustrialApiary(this.mName, this.mTier, this.mDescriptionArray, this.mTextures, this.mGUIName, this.mNEIName);
    }


    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_IndustrialApiary(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_IndustrialApiary(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mSpeed", mSpeed);
        aNBT.setBoolean("mLockedSpeed", mLockedSpeed);
        if(usedQueen != null)
            aNBT.setTag("usedQueen", usedQueen.writeToNBT(new NBTTagCompound()));
        aNBT.setBoolean("retrievingPollenInThisOperation", retrievingPollenInThisOperation);
        aNBT.setInteger("pollinationDelay", pollinationDelay);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mSpeed = aNBT.getInteger("mSpeed");
        mLockedSpeed = aNBT.getBoolean("mLockedSpeed");
        if(aNBT.hasKey("usedQueen"))
            usedQueen = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("usedQueen"));
        retrievingPollenInThisOperation =  aNBT.getBoolean("retrievingPollenInThisOperation");
        pollinationDelay = aNBT.getInteger("pollinationDelay");
    }

    boolean retrievingPollenInThisOperation = false;
    IIndividual retrievedpollen = null;
    int pollinationDelay = 100;

    @Override
    public int checkRecipe() {
        updateModifiers();
        if(canWork()) {

            ItemStack queen = getQueen();
            usedQueen = queen.copy();
            if(beeRoot.getType(queen) == EnumBeeType.QUEEN)
            {
                IBee bee = beeRoot.getMember(queen);
                usedQueenBee = bee;

                // LIFE CYCLES

                float mod = this.getLifespanModifier(null, null, 1.f);
                IBeekeepingMode mode = beeRoot.getBeekeepingMode(this.getWorld());
                IBeeModifier beemodifier = mode.getBeeModifier();
                mod *= beemodifier.getLifespanModifier(null, null, 1.f);
                int h = bee.getHealth();
                mod = 1.f / mod;
                float cycles = h/mod;


                // PRODUCTS

                HashMap<GT_Utility.ItemId, ItemStack> pollen = new HashMap<>();

                if(isRetrievingPollen && floweringMod > 0f) {
                    int icycles = (int)cycles + (getWorld().rand.nextFloat() < (cycles - (float)((int)cycles)) ? 1 : 0);
                    for(int z = 0; z < icycles; z++) {
                        IIndividual p = bee.retrievePollen(this);
                        if(p != null)
                        {
                            ItemStack s = p.getGenome().getSpeciesRoot().getMemberStack(p, EnumGermlingType.POLLEN.ordinal());
                            if(s != null) {
                                GT_Utility.ItemId id = GT_Utility.ItemId.createNoCopy(s);
                                pollen.computeIfAbsent(id, k -> { ItemStack ns = s.copy(); ns.stackSize = 0; return ns; });
                                pollen.get(id).stackSize += s.stackSize;
                            }
                        }
                    }
                }

                retrievedpollen = null;
                retrievingPollenInThisOperation = isRetrievingPollen;

                IBeeGenome genome =  bee.getGenome();
                IAlleleBeeSpecies primary = genome.getPrimary();
                IAlleleBeeSpecies secondary = genome.getSecondary();

                float speed = genome.getSpeed() * getProductionModifier(null, 1f) * beemodifier.getProductionModifier(null, 1.f);

                HashMap<GT_Utility.ItemId, Float> drops = new HashMap<>();
                HashMap<GT_Utility.ItemId, ItemStack> dropstacks = new HashMap<>();

                for(Map.Entry<ItemStack, Float> entry : primary.getProductChances().entrySet()) {
                    GT_Utility.ItemId id = GT_Utility.ItemId.createNoCopy(entry.getKey());
                    drops.merge(id, Math.min(1f, entry.getValue() * speed) * (float) entry.getKey().stackSize * cycles, Float::sum);
                    dropstacks.computeIfAbsent(id, k -> entry.getKey());
                }
                for(Map.Entry<ItemStack, Float> entry : secondary.getProductChances().entrySet()) {
                    GT_Utility.ItemId id = GT_Utility.ItemId.createNoCopy(entry.getKey());
                    drops.merge(id, Math.min(1f, (float) Math.round(entry.getValue() / 2.0F) * speed) * (float) entry.getKey().stackSize * cycles, Float::sum);
                    dropstacks.computeIfAbsent(id, k -> entry.getKey());
                }
                if(primary.isJubilant(genome, this) && secondary.isJubilant(genome, this))
                    for(Map.Entry<ItemStack, Float> entry : primary.getSpecialtyChances().entrySet()) {
                        GT_Utility.ItemId id = GT_Utility.ItemId.createNoCopy(entry.getKey());
                        drops.merge(id, Math.min(1f, entry.getValue() * speed) * (float) entry.getKey().stackSize * cycles, Float::sum);
                        dropstacks.computeIfAbsent(id, k -> entry.getKey());
                    }

                int i = 0;
                int imax = mOutputItems.length;

                IApiaristTracker breedingTracker = beeRoot.getBreedingTracker(getWorld(), getOwner());

                if(!bee.canSpawn()) {
                    ItemStack convert = new ItemStack(PluginApiculture.items.beePrincessGE);
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    queen.writeToNBT(nbttagcompound);
                    convert.setTagCompound(nbttagcompound);
                    this.mOutputItems[i++] = convert;
                }
                else {
                    IBee b = bee.spawnPrincess(this);
                    if(b != null){
                        ItemStack princess = beeRoot.getMemberStack(b, EnumBeeType.PRINCESS.ordinal());
                        breedingTracker.registerPrincess(b);
                        this.mOutputItems[i++] = princess;
                    }
                    IBee[] d = bee.spawnDrones(this);
                    if(d != null && d.length > 0) {
                        HashMap<GT_Utility.ItemId, ItemStack> drones = new HashMap<>(d.length);
                        for(IBee dr : d) {
                            ItemStack drone = beeRoot.getMemberStack(dr, EnumBeeType.DRONE.ordinal());
                            breedingTracker.registerDrone(dr);
                            GT_Utility.ItemId drid = GT_Utility.ItemId.createNoCopy(drone);
                            if(drones.containsKey(drid)) drones.get(drid).stackSize += drone.stackSize;
                            else {
                                this.mOutputItems[i++] = drone;
                                drones.put(drid, drone);
                            }
                        }
                    }
                }

                int imin = i;

                setQueen(null);

                for(Map.Entry<GT_Utility.ItemId, Float> entry : drops.entrySet())
                {
                    ItemStack s = dropstacks.get(entry.getKey()).copy();
                    s.stackSize = entry.getValue().intValue() + (getWorld().rand.nextFloat() < (entry.getValue() - (float) entry.getValue().intValue()) ? 1 : 0);
                    if(s.stackSize > 0 && i < imax)
                        while(true) {
                            if (s.stackSize <= s.getMaxStackSize()) {
                                this.mOutputItems[i++] = s;
                                break;
                            } else
                                this.mOutputItems[i++] = s.splitStack(s.getMaxStackSize());
                            if(i >= imax)
                                break;
                        }
                }

                for(ItemStack s : pollen.values())
                    if(i < imax)
                        this.mOutputItems[i++] = s;
                    else
                        break;

                // Overclock


                this.mMaxProgresstime = (int)(cycles * 550.f);
                int timemaxdivider = this.mMaxProgresstime / 100;
                int useddivider = 1 << this.mSpeed;
                int actualdivider = useddivider;
                this.mMaxProgresstime /= Math.min(actualdivider, timemaxdivider);
                actualdivider /= Math.min(actualdivider, timemaxdivider);
                for(i--; i >= imin; i--)
                    this.mOutputItems[i].stackSize *= actualdivider;

                pollinationDelay = Math.max((int)(this.mMaxProgresstime / cycles), 20); // don't run too often

                this.mProgresstime = 0;
                this.mEUt = (int)((float)baseEUtUsage * this.energyMod * useddivider);
                if(useddivider == 2)
                    this.mEUt += 32;
                else if(useddivider > 2)
                    this.mEUt += (32 * (useddivider << (this.mSpeed - 2)));
            }
            else {
                // Breeding time

                retrievingPollenInThisOperation = true; // Don't pollinate when breeding

                this.mMaxProgresstime = 100;
                this.mProgresstime = 0;
                int useddivider = Math.min(100, 1 << this.mSpeed);
                this.mMaxProgresstime /= useddivider;
                this.mEUt = (int)((float)baseEUtUsage * this.energyMod * useddivider);
                if(useddivider == 2)
                    this.mEUt += 32;
                else if(useddivider > 2)
                    this.mEUt += (32 * (useddivider << (this.mSpeed - 2)));

                IBee princess = beeRoot.getMember(getQueen());
                usedQueenBee = princess;
                IBee drone = beeRoot.getMember(getDrone());
                princess.mate(drone);
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                princess.writeToNBT(nbttagcompound);
                this.mOutputItems[0] = new ItemStack(PluginApiculture.items.beeQueenGE);
                this.mOutputItems[0].setTagCompound(nbttagcompound);
                beeRoot.getBreedingTracker(getWorld(), getOwner()).registerQueen(princess);

                setQueen(null);
                getDrone().stackSize -= 1;
                if(getDrone().stackSize == 0)
                    setDrone(null);

            }


            return FOUND_AND_SUCCESSFULLY_USED_RECIPE;
        }

        return DID_NOT_FIND_RECIPE;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        updateModifiers();
    }

    @Override
    protected boolean hasEnoughEnergyToCheckRecipe() {
        return getBaseMetaTileEntity().isUniversalEnergyStored(V[mSpeed] * 8L);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isClientSide()) {
            if(GT_Client.changeDetected == 4) {
                /* Client tick counter that is set to 5 on hiding pipes and covers.
                 * It triggers a texture update next client tick when reaching 4, with provision for 3 more update tasks,
                 * spreading client change detection related work and network traffic on different ticks, until it reaches 0.
                 */
                aBaseMetaTileEntity.issueTextureUpdate();
            }
            if(aBaseMetaTileEntity.isActive()){
                if(usedQueen != null) {
                    if (aTick % 2 == 0) {
                        // FX on client, effect on server
                        IBee bee = beeRoot.getMember(usedQueen);
                        effectData = bee.doFX(effectData, this);
                    }
                }
            }
        }
        if(aBaseMetaTileEntity.isServerSide()){

            mCharge = aBaseMetaTileEntity.getStoredEU() / 2 > aBaseMetaTileEntity.getEUCapacity() / 3;
            mDecharge = aBaseMetaTileEntity.getStoredEU() < aBaseMetaTileEntity.getEUCapacity() / 3;

            doDisplayThings();

            if(!aBaseMetaTileEntity.isActive())
            {
                if(aBaseMetaTileEntity.isAllowedToWork() && (aBaseMetaTileEntity.hasInventoryBeenModified() || aTick % 600 == 0 || aBaseMetaTileEntity.hasWorkJustBeenEnabled()) && hasEnoughEnergyToCheckRecipe())
                {
                    int check = checkRecipe();
                    if(check == FOUND_AND_SUCCESSFULLY_USED_RECIPE) {
                        aBaseMetaTileEntity.setActive(true);
                    }
                }
            }
            else{

                if(this.mProgresstime < 0)
                {
                    this.mProgresstime++;
                    return;
                }
                if(this.mStuttering)
                {
                    if(!aBaseMetaTileEntity.isAllowedToWork())
                        return;
                    if(aTick % 100 == 0)
                        this.mStuttering = false;
                    return;
                }
                if(this.hasErrors())
                {
                    if(!aBaseMetaTileEntity.isAllowedToWork())
                        return;
                    if(aTick % 100 == 0)
                        if(!canWork(usedQueen))
                            this.stutterProcess();
                    return;
                }

                if(!drainEnergyForProcess(this.mEUt))
                {
                    this.mStuttering = true;
                    this.stutterProcess();
                    return;
                }
                this.mProgresstime++;
                if(usedQueen != null) {
                    if(usedQueenBee == null)
                        usedQueenBee = beeRoot.getMember(usedQueen);
                    effectData = usedQueenBee.doEffect(effectData, this);
                    if(!retrievingPollenInThisOperation && floweringMod > 0f && this.mProgresstime % pollinationDelay == 0)
                    {
                        if(retrievedpollen == null)
                            retrievedpollen = usedQueenBee.retrievePollen(this);
                        if(retrievedpollen != null && (usedQueenBee.pollinateRandom(this, retrievedpollen) || this.mProgresstime % (pollinationDelay * 5) == 0))
                            retrievedpollen = null;
                    }
                }

                if(this.mProgresstime % 100 == 0)
                {
                    if(!canWork(usedQueen))
                    {
                        this.stutterProcess();
                        return;
                    }
                }

                if(this.mProgresstime >= this.mMaxProgresstime)
                {
                    updateModifiers();
                    for (int i = 0; i < mOutputItems.length; i++)
                        if(mOutputItems[i] != null)
                            for (int j = 0; j < mOutputItems.length; j++) {
                                if(j == 0 && isAutomated)
                                {
                                    if(beeRoot.isMember(mOutputItems[i], EnumBeeType.QUEEN.ordinal()) || beeRoot.isMember(mOutputItems[i], EnumBeeType.PRINCESS.ordinal())) {
                                        if(aBaseMetaTileEntity.addStackToSlot(queen, mOutputItems[i]))
                                            break;
                                    }
                                    else if(beeRoot.isMember(mOutputItems[i], EnumBeeType.DRONE.ordinal()))
                                        if(aBaseMetaTileEntity.addStackToSlot(drone, mOutputItems[i]))
                                            break;
                                }
                                else if(i == 0 && j == 0 && beeRoot.isMember(mOutputItems[0], EnumBeeType.QUEEN.ordinal()))
                                    if(aBaseMetaTileEntity.addStackToSlot(queen, mOutputItems[0]))
                                        break;
                                if (aBaseMetaTileEntity.addStackToSlot(getOutputSlot() + ((j + i) % mOutputItems.length), mOutputItems[i]))
                                    break;
                            }
                    Arrays.fill(mOutputItems, null);
                    mEUt = 0;
                    mProgresstime = 0;
                    mMaxProgresstime = 0;
                    mStuttering = false;
                    aBaseMetaTileEntity.setActive(false);

                    if (doesAutoOutput() && !isOutputEmpty() && aBaseMetaTileEntity.getFrontFacing() != mMainFacing) {
                        TileEntity tTileEntity2 = aBaseMetaTileEntity.getTileEntityAtSide(aBaseMetaTileEntity.getFrontFacing());
                        long tStoredEnergy = aBaseMetaTileEntity.getUniversalEnergyStored();
                        int tMaxStacks = (int)(tStoredEnergy/64L);
                        if (tMaxStacks > mOutputItems.length)
                            tMaxStacks = mOutputItems.length;

                        moveMultipleItemStacks(aBaseMetaTileEntity, tTileEntity2, aBaseMetaTileEntity.getFrontFacing(), aBaseMetaTileEntity.getBackFacing(), null, false, (byte) 64, (byte) 1, (byte) 64, (byte) 1,tMaxStacks);
                    }

                    if(aBaseMetaTileEntity.isAllowedToWork() && checkRecipe() == FOUND_AND_SUCCESSFULLY_USED_RECIPE)
                        aBaseMetaTileEntity.setActive(true);
                }
            }
        }
    }

    public void cancelProcess(){
        if(this.getBaseMetaTileEntity().isActive() && this.getBaseMetaTileEntity().isServerSide() && usedQueen != null && beeRoot.isMember(usedQueen, EnumBeeType.QUEEN.ordinal()))
        {
            Arrays.fill(mOutputItems, null);
            mEUt = 0;
            mProgresstime = 0;
            mMaxProgresstime = 0;
            mStuttering = false;
            this.getBaseMetaTileEntity().setActive(false);
            setQueen(usedQueen);
            this.getBaseMetaTileEntity().disableWorking();
        }
    }

    @Override
    public boolean isItemValidForSlot(int aIndex, ItemStack aStack) {
        if(aStack == null) return false;
        if(aIndex < getInputSlot())
            return true;
        if(aIndex == queen) return beeRoot.isMember(aStack, EnumBeeType.QUEEN.ordinal()) || beeRoot.isMember(aStack, EnumBeeType.PRINCESS.ordinal());
        else if(aIndex == drone) return beeRoot.isMember(aStack, EnumBeeType.DRONE.ordinal());
        else if(aIndex < getOutputSlot()) {
            if(!(aStack.getItem() instanceof IApiaryUpgrade) && !GT_ApiaryUpgrade.isUpgrade(aStack))
                return false;
            for(int i = drone+1; i < drone+1+4; i++)
            {
                if(aIndex == i)
                    continue;
                ItemStack s = getStackInSlot(i);
                if(s == null)
                    continue;
                if(GT_Utility.areStacksEqual(getStackInSlot(i), aStack))
                    return false;
                if(GT_ApiaryUpgrade.isUpgrade(aStack))
                {
                    if(!GT_ApiaryUpgrade.getUpgrade(aStack).isAllowedToWorkWith(getStackInSlot(i)))
                        return false;
                }
                else if(GT_ApiaryUpgrade.isUpgrade(s))
                {
                    if(!GT_ApiaryUpgrade.getUpgrade(s).isAllowedToWorkWith(aStack))
                        return false;
                }
            }
            return true;
        }
        else return false;

    }

    @Override
    public void setInventorySlotContents(int aIndex, ItemStack aStack) {
        if(aIndex == queen && aStack != null && getBaseMetaTileEntity().isClientSide())
            usedQueen = aStack.copy();
        super.setInventorySlotContents(aIndex, aStack);
    }

    // Gets called on slot click //
    public void onInventoryUpdate(int aIndex){
        if(aIndex > drone && aIndex < getOutputSlot())
            updateModifiers();
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getUsedQueen(){
        return usedQueen;
    }

    //region IBeeHousing

    @Override
    public Iterable<IBeeModifier> getBeeModifiers() {
        return Collections.singletonList(this);
    }

    @Override
    public Iterable<IBeeListener> getBeeListeners() {
        return Collections.singletonList(this);
    }

    @Override
    public IBeeHousingInventory getBeeInventory() {
        return this;
    }

    @Override
    public IBeekeepingLogic getBeekeepingLogic() {
        return dummylogic;
    }

    @Override
    public int getBlockLightValue() {
        return this.getBaseMetaTileEntity().getLightLevelAtSide((byte) 1);
    }

    @Override
    public boolean canBlockSeeTheSky() {
        return this.getBaseMetaTileEntity().getSkyAtSide((byte)1);
    }

    @Override
    public World getWorld() {
        return this.getBaseMetaTileEntity().getWorld();
    }

    GameProfile owner = null;

    @Override
    public GameProfile getOwner() {
        if(owner == null)
            owner = new GameProfile(this.getBaseMetaTileEntity().getOwnerUuid(), this.getBaseMetaTileEntity().getOwnerName());
        return owner;
    }

    @Override
    public Vec3 getBeeFXCoordinates() {
        return Vec3.createVectorHelper(getBaseMetaTileEntity().getXCoord() + 0.5, getBaseMetaTileEntity().getYCoord() + 0.5, getBaseMetaTileEntity().getZCoord() + 0.5);
    }

    @Override
    public BiomeGenBase getBiome() {
        if(biomeOverride == null)
            return this.getBaseMetaTileEntity().getBiome();
        return biomeOverride;
    }

    @Override
    public EnumTemperature getTemperature() {
        if(BiomeHelper.isBiomeHellish(getBiome()))
            return EnumTemperature.HELLISH;
        return
            EnumTemperature.getFromValue(getBiome().temperature + temperatureMod);
    }

    @Override
    public EnumHumidity getHumidity() {
        return EnumHumidity.getFromValue(getBiome().rainfall + humidityMod);
    }

    @Override
    public IErrorLogic getErrorLogic() {
        return this;
    }

    @Override
    public ChunkCoordinates getCoordinates() {
        return this.getBaseMetaTileEntity().getCoords();
    }

    //endregion

//region IBeeHousingInventory
    @Override
    public ItemStack getQueen() {
        return getStackInSlot(queen);
    }

    @Override
    public ItemStack getDrone() {
        return getStackInSlot(drone);
    }

    @Override
    public void setQueen(ItemStack itemStack) {
        setInventorySlotContents(queen, itemStack);
    }

    @Override
    public void setDrone(ItemStack itemStack) {
        setInventorySlotContents(drone, itemStack);
    }

    @Override
    public boolean addProduct(ItemStack itemStack, boolean b) {
        throw new RuntimeException("Should not happen :F");
    }
//endregion

//region IErrorLogic

    public HashSet<IErrorState> mErrorStates = new HashSet<>();

    @Override
    public boolean setCondition(boolean b, IErrorState iErrorState) {
        if(b)
            mErrorStates.add(iErrorState);
        else
            mErrorStates.remove(iErrorState);
        return b;
    }

    @Override
    public boolean contains(IErrorState iErrorState) {
        return mErrorStates.contains(iErrorState);
    }

    @Override
    public boolean hasErrors() {
        return !mErrorStates.isEmpty();
    }

    @Override
    public void clearErrors() {
        mErrorStates.clear();
    }

    @Override
    public void writeData(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.write(mErrorStates.size());
        for(IErrorState s : mErrorStates)
            dataOutputStream.writeUTF(s.getUniqueName());
    }

    @Override
    public void readData(DataInputStream dataInputStream) throws IOException {
        for(int i = dataInputStream.readInt(); i > 0; i--)
            mErrorStates.add(ForestryAPI.errorStateRegistry.getErrorState(dataInputStream.readUTF()));
    }

    @Override
    public ImmutableSet<IErrorState> getErrorStates() {
        return ImmutableSet.copyOf(mErrorStates);
    }

    private String flowerType = "";
    private ChunkCoordinates flowercoords = null;
    private Block flowerBlock;
    private int flowerBlockMeta;


    private boolean checkFlower(IBee bee){
        String flowerType = bee.getGenome().getFlowerProvider().getFlowerType();
        if(!this.flowerType.equals(flowerType))
            flowercoords = null;
        if(flowercoords != null) {
            if(     getWorld().getBlock(flowercoords.posX, flowercoords.posY, flowercoords.posZ) != flowerBlock
                ||  getWorld().getBlockMetadata(flowercoords.posX, flowercoords.posY, flowercoords.posZ) != flowerBlockMeta)
                if (!FlowerManager.flowerRegistry.isAcceptedFlower(flowerType, getWorld(), flowercoords.posX, flowercoords.posY, flowercoords.posZ))
                    flowercoords = null;
                else
                {
                    flowerBlock = getWorld().getBlock(flowercoords.posX, flowercoords.posY, flowercoords.posZ);
                    flowerBlockMeta = getWorld().getBlockMetadata(flowercoords.posX, flowercoords.posY, flowercoords.posZ);
                }
        }
        if(flowercoords == null) {
            flowercoords = FlowerManager.flowerRegistry.getAcceptedFlowerCoordinates(this, bee, flowerType);
            if(flowercoords != null)
            {
                flowerBlock = getWorld().getBlock(flowercoords.posX, flowercoords.posY, flowercoords.posZ);
                flowerBlockMeta = getWorld().getBlockMetadata(flowercoords.posX, flowercoords.posY, flowercoords.posZ);
                this.flowerType = flowerType;
            }
        }
        return flowercoords != null;
    }

    private boolean canWork(ItemStack queen){
        clearErrors();
        if(queen == null)
            return true; // Reloaded the chunk ?
        if(beeRoot.isMember(queen, EnumBeeType.PRINCESS.ordinal()))
            return true;
        IBee bee = beeRoot.getMember(queen);
        for(IErrorState err : bee.getCanWork(this))
            setCondition(true, err);
        setCondition(!checkFlower(bee), EnumErrorCode.NO_FLOWER);
        return !hasErrors();
    }

    private boolean canWork(){
        clearErrors();
        EnumBeeType beeType = beeRoot.getType(getQueen());
        if(beeType == EnumBeeType.PRINCESS)
        {
            setCondition(!beeRoot.isDrone(getDrone()), EnumErrorCode.NO_DRONE);
            return !hasErrors();
        }
        if(beeType == EnumBeeType.QUEEN)
        {
            IBee bee = beeRoot.getMember(getQueen());
            for(IErrorState err : bee.getCanWork(this))
                setCondition(true, err);
            setCondition(!checkFlower(bee), EnumErrorCode.NO_FLOWER);
            return !hasErrors();
        }
        else
        {
            setCondition(true, EnumErrorCode.NO_QUEEN);
            return false;
        }
    }


//endregion

    //region IBeeModifier

    private float terrorityMod = 1f;
    private float mutationMod = 1f;
    private float lifespanMod = 1f;
    private float productionMod = 1f;
    private float floweringMod = 1f;
    private float geneticDecayMod = 1f;
    private float energyMod = 1f;
    private boolean sealedMod = false;
    private boolean selfLightedMod = false;
    private boolean sunlightSimulatedMod = false;
    private BiomeGenBase biomeOverride = null;
    private float humidityMod = 0f;
    private float temperatureMod = 0f;
    private boolean isAutomated = false;
    private boolean isRetrievingPollen = false;

    private int maxspeed = 0;

    public void updateModifiers(){
        maxspeed = 0;
        ApiaryModifiers mods = new ApiaryModifiers();
        for(int i = 2; i < 2+4; i++)
        {
            ItemStack s = getInputAt(i);
            if(s == null)
                continue;
            if(s.getItem() instanceof IApiaryUpgrade) {
                IApiaryUpgrade up = (IApiaryUpgrade) s.getItem();
                up.applyModifiers(mods, s);
            }
            else if(GT_ApiaryUpgrade.isUpgrade(s))
            {
                GT_ApiaryUpgrade upgrade = GT_ApiaryUpgrade.getUpgrade(s);
                if(upgrade != null)
                {
                    maxspeed = upgrade.applyMaxSpeedModifier(maxspeed);
                    upgrade.applyModifiers(mods, s);
                }
            }
        }



        terrorityMod = mods.territory;
        mutationMod = mods.mutation;
        lifespanMod = mods.lifespan;
        productionMod = mods.production;
        floweringMod = mods.flowering;
        geneticDecayMod = mods.geneticDecay;
        energyMod = mods.energy;
        sealedMod = mods.isSealed;
        selfLightedMod = mods.isSelfLighted;
        sunlightSimulatedMod = mods.isSunlightSimulated;
        biomeOverride = mods.biomeOverride;
        humidityMod = mods.humidity;
        temperatureMod = mods.temperature;
        isAutomated = mods.isAutomated;
        isRetrievingPollen = mods.isCollectingPollen;

        if(mLockedSpeed)
            mSpeed = maxspeed;
        else
            mSpeed = Math.min(mSpeed, maxspeed);
    }

    @Override
    public float getTerritoryModifier(IBeeGenome iBeeGenome, float v) {
        return Math.min(5, terrorityMod);
    }

    @Override
    public float getMutationModifier(IBeeGenome iBeeGenome, IBeeGenome iBeeGenome1, float v) {
        return mutationMod;
    }

    @Override
    public float getLifespanModifier(IBeeGenome iBeeGenome, IBeeGenome iBeeGenome1, float v) {
        return lifespanMod;
    }

    @Override
    public float getProductionModifier(IBeeGenome iBeeGenome, float v) {
        return productionMod;
    }

    @Override
    public float getFloweringModifier(IBeeGenome iBeeGenome, float v) {
        return floweringMod;
    }

    @Override
    public float getGeneticDecay(IBeeGenome iBeeGenome, float v) {
        return geneticDecayMod;
    }

    public float getEnergyModifier() {
        return energyMod;
    }

    @Override
    public boolean isSealed() {
        return sealedMod;
    }

    @Override
    public boolean isSelfLighted() {
        return selfLightedMod;
    }

    @Override
    public boolean isSunlightSimulated() {
        return sunlightSimulatedMod;
    }


    @Override
    public boolean isHellish() {
        return getBiome() == BiomeGenBase.hell;
    }

    public int getMaxSpeed(){
        return maxspeed;
    }

    //endregion

    //region IBeeListener

    @Override
    public void wearOutEquipment(int i) {

    }

    @Override
    public void onQueenDeath() {

    }

    @Override
    public boolean onPollenRetrieved(IIndividual iIndividual) {
        return false;
    }


    //endregion

    static final IBeekeepingLogic dummylogic = new IBeekeepingLogic() {
        @Override
        public boolean canWork() {
            return true;
        }

        @Override
        public void doWork() {

        }

        @Override
        public void syncToClient() {

        }

        @Override
        public void syncToClient(EntityPlayerMP entityPlayerMP) {

        }

        @Override
        public int getBeeProgressPercent() {
            return 0;
        }

        @Override
        public boolean canDoBeeFX() {
            return false;
        }

        @Override
        public void doBeeFX() {

        }

        @Override
        public void readFromNBT(NBTTagCompound nbtTagCompound) {

        }

        @Override
        public void writeToNBT(NBTTagCompound nbtTagCompound) {

        }
    };


}
