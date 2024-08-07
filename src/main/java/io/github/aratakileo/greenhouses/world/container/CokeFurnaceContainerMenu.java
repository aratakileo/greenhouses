package io.github.aratakileo.greenhouses.world.container;

import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.world.container.SimpleContainerMenu;
import io.github.aratakileo.elegantia.world.slot.ElegantedSlot;
import io.github.aratakileo.elegantia.world.slot.FluidSlotController;
import io.github.aratakileo.elegantia.world.slot.SlotController;
import io.github.aratakileo.greenhouses.world.item.ModItems;
import io.github.aratakileo.greenhouses.util.CokeFurnaceUtil;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CokeFurnaceContainerMenu extends SimpleContainerMenu<CokeFurnaceUtil.CokeFurnaceContainerData> {
    public final static Supplier<TextureDrawable> INGREDIENT_SLOT_ICON
            = () -> TextureDrawable.of(CokeFurnaceUtil.GUI_TEXTURE).setUV(176, 16),
            CREOSOTE_SLOT_ICON = () -> TextureDrawable.of(CokeFurnaceUtil.GUI_TEXTURE).setUV(176, 32);

    public CokeFurnaceContainerMenu(int syncId, @NotNull Inventory playerInventory) {
        this(
                playerInventory,
                new SimpleContainer(CokeFurnaceUtil.TOTAL_SLOTS),
                new CokeFurnaceUtil.CokeFurnaceContainerData(),
                syncId
        );
    }

    public CokeFurnaceContainerMenu(
            @NotNull Inventory playerInventory,
            @NotNull Container container,
            @NotNull CokeFurnaceUtil.CokeFurnaceContainerData data,
            int syncId
    ) {
        super(ContainerMenus.COKE_FURNACE_CONTAINER_MENU, container, data, syncId);

        checkContainerSize(container, CokeFurnaceUtil.TOTAL_SLOTS);
        checkContainerDataCount(data, CokeFurnaceUtil.CONTAINER_DATA_SIZE);

        addSlot(new ElegantedSlot(
                container,
                SlotController.filtered(CokeFurnaceUtil::isIngredient),
                CokeFurnaceUtil.INGREDIENT_SLOT,
                42,
                35
        ).setIconGetter(INGREDIENT_SLOT_ICON));

        addSlot(new ElegantedSlot(
                container,
                SlotController.RESULT,
                CokeFurnaceUtil.RESULT_SLOT,
                82,
                35
        ));

        final var FLUID_SLOT_CONTROLLER = new FluidSlotController.Builder(
                ModItems.CREOSOTE_BUCKET,
                () -> data.producedCreosote += CokeFurnaceUtil.CREOSOTE_PER_BUCKET,
                () -> data.producedCreosote -= CokeFurnaceUtil.CREOSOTE_PER_BUCKET
        )
                .setMayInsertFluid(data::canInsertCreosote)
                .setMayTakeFluid(data::canTakeCreosote)
                .build();

        addSlot(new ElegantedSlot(
                container,
                FLUID_SLOT_CONTROLLER,
                CokeFurnaceUtil.CREOSOTE_SLOT,
                139,
                57
        ).setIconGetter(CREOSOTE_SLOT_ICON));

        addPlayerInventorySlots(playerInventory);
        addPlayerHotbarSlots(playerInventory);

        addDataSlots(data);
    }

    @Override
    public void clicked(int slotIndex, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (clickType != ClickType.QUICK_MOVE || slotIndex < CokeFurnaceUtil.TOTAL_SLOTS) {
            super.clicked(slotIndex, button, clickType, player);
            return;
        }

        final var sourceSlot = slots.get(slotIndex);
        final var sourceSlotItem = sourceSlot.getItem();

        for (var cokeFurnaceSlotIndex = 0; cokeFurnaceSlotIndex < CokeFurnaceUtil.TOTAL_SLOTS; cokeFurnaceSlotIndex++) {
            final var currentSlot = getSlot(cokeFurnaceSlotIndex);

            if (!currentSlot.mayPlace(sourceSlotItem)) continue;

            final var remainingSlotStack = currentSlot.safeInsert(sourceSlotItem);
            sourceSlot.set(remainingSlotStack);
            sourceSlot.setChanged();
            return;
        }

        super.clicked(slotIndex, button, clickType, player);
    }

    public float getProgress() {
        return (float) data.progress / (float) data.maxProgress;
    }

    public float getProducedCreosoteScale() {
        return (float) data.producedCreosote / (float) CokeFurnaceUtil.MAX_PRODUCED_CREOSOTE;
    }

    public int getProducedCreosote() {
        return data.producedCreosote;
    }
}
