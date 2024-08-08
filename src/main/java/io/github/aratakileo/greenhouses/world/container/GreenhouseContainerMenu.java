package io.github.aratakileo.greenhouses.world.container;

import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.world.container.SimpleContainerMenu;
import io.github.aratakileo.elegantia.world.slot.ElegantedSlot;
import io.github.aratakileo.elegantia.world.slot.FluidSlotController;
import io.github.aratakileo.elegantia.world.slot.SlotController;
import io.github.aratakileo.greenhouses.Greenhouses;
import io.github.aratakileo.greenhouses.util.GreenhouseUtil;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class GreenhouseContainerMenu extends SimpleContainerMenu<GreenhouseUtil.GreenhouseContainerData> {
    private final static Supplier<TextureDrawable> PLANT_SLOT_ICON
            = () -> TextureDrawable.of(GreenhouseUtil.GUI_TEXTURE).setUV(176, 64),
            GROUND_SLOT_ICON = () -> TextureDrawable.of(GreenhouseUtil.GUI_TEXTURE).setUV(176, 80),
            WATER_SLOT_ICON = () -> TextureDrawable.of(GreenhouseUtil.GUI_TEXTURE).setUV(176, 48);

    public GreenhouseContainerMenu(int syncId, @NotNull Inventory inventory) {
        this(
                inventory,
                new SimpleContainer(GreenhouseUtil.TOTAL_SLOTS),
                new GreenhouseUtil.GreenhouseContainerData(),
                syncId
        );
    }

    public GreenhouseContainerMenu(
            @NotNull Inventory playerInventory,
            @NotNull Container container,
            @NotNull GreenhouseUtil.GreenhouseContainerData data,
            int syncId
    ) {
        super(ContainerMenus.GREENHOUSE_CONTAINER_MENU, container, data, syncId);

        checkContainerSize(container, GreenhouseUtil.TOTAL_SLOTS);
        checkContainerDataCount(data, GreenhouseUtil.CONTAINER_DATA_SIZE);

        addSlot(new ElegantedSlot(
                container,
                new SlotController.Builder().setSingeItemMaxStackSize().setMayPlace(GreenhouseUtil::isPlant).build(),
                GreenhouseUtil.PLANT_INPUT_SLOT,
                70,
                24
        ).setIconGetter(PLANT_SLOT_ICON));

        addSlot(new ElegantedSlot(
                container,
                new GroundSlotController(),
                GreenhouseUtil.GROUND_INPUT_SLOT,
                70,
                45
        ).setIconGetter(GROUND_SLOT_ICON));

        final var FLUID_SLOT_CONTROLLER = new FluidSlotController.Builder(
                Items.WATER_BUCKET,
                () -> data.hasWater = true,
                () -> data.hasWater = false
        ).setMayTakeFluid(this::isGroundWet).build();

        addSlot(new ElegantedSlot(
                container,
                FLUID_SLOT_CONTROLLER,
                GreenhouseUtil.WATER_INPUT_SLOT,
                50,
                35
        ).setIconGetter(WATER_SLOT_ICON));

        for (var i = GreenhouseUtil.INPUT_SLOTS; i < GreenhouseUtil.TOTAL_SLOTS; i++) {
            final var localIndex = i - GreenhouseUtil.INPUT_SLOTS;
            addSlot(new ElegantedSlot(
                    container,
                    SlotController.RESULT,
                    i,
                    124,
                    17 * (localIndex + 1) + localIndex
            ));
        }

        addPlayerInventorySlots(playerInventory);
        addPlayerHotbarSlots(playerInventory);

        addDataSlots(data);
    }

    @Override
    public void clicked(int slotIndex, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotIndex < 0) {
            super.clicked(slotIndex, button, clickType, player);
            return;
        }

        final var sourceSlot = slots.get(slotIndex);

        if (
                slotIndex == GreenhouseUtil.GROUND_INPUT_SLOT
                        && sourceSlot instanceof ElegantedSlot slot
                        && slot.controller instanceof GroundSlotController controller
        ) {
            final var carriedItem = getCarried();

            if (clickType == ClickType.PICKUP) {
                if (!carriedItem.isEmpty() && GroundSlotController.mayUseHoe(slot.getItem(), carriedItem)) {
                    GroundSlotController.useHoe(carriedItem, slot::set);
                    return;
                }

                GroundSlotController.prepareBeforePickup(slot.getItem(), slot::set);
            }

            if (clickType == ClickType.QUICK_MOVE)
                GroundSlotController.prepareBeforePickup(slot.getItem(), slot::set);
        }

        if (slotIndex >= GreenhouseUtil.TOTAL_SLOTS && clickType == ClickType.QUICK_MOVE) {
            if (!sourceSlot.hasItem() || !sourceSlot.mayPickup(player)) return;

            final var sourceSlotItem = sourceSlot.getItem();

            for (var greenhouseSlotIndex = 0; greenhouseSlotIndex < GreenhouseUtil.TOTAL_SLOTS; greenhouseSlotIndex++) {
                final var currentSlot = getSlot(greenhouseSlotIndex);

                if (!currentSlot.mayPlace(sourceSlotItem)) continue;

                Greenhouses.LOGGER.warn("Before move: {}", currentSlot.getItem().getDisplayName().getString());

                final var remainingSlotStack = currentSlot.safeInsert(sourceSlotItem);
                sourceSlot.set(remainingSlotStack);
                sourceSlot.setChanged();

                Greenhouses.LOGGER.warn("After move: {}", currentSlot.getItem().getDisplayName().getString());
                return;
            }
            return;
        }

        super.clicked(slotIndex, button, clickType, player);
    }

    public boolean isGroundWet() {
        return data.hasWater;
    }

    public float getProgress() {
        return (float) data.progress / (float) data.maxProgress;
    }

    public @NotNull GreenhouseUtil.FailType getFailCode() {
        return data.failType;
    }

    public boolean isInvalidRecipe() {
        return data.failType != GreenhouseUtil.FailType.NONE;
    }
}
