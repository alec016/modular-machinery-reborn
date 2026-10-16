package es.degrassi.mmr.common.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.util.EnergyDisplayUtil;
import es.degrassi.mmr.common.block.prop.ConfigLoaded;
import es.degrassi.mmr.common.data.Config;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.MachineJsonReloadListener;
import es.degrassi.mmr.common.manager.crafting.MachineProcessorCore;
import es.degrassi.mmr.common.network.server.SOpenFilePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.util.Util;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class MMRCommand {
  private MMRCommand() {}
  public static LiteralArgumentBuilder<CommandSourceStack> register(String name) {
    return Commands.literal(name)
      .then(logging())
      .then(reload());
  }

  private static ArgumentBuilder<CommandSourceStack, ?> logging() {
    return Commands.literal("log")
      .requires(cs -> cs.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
      .executes(ctx -> {
        if (ctx.getSource().getEntity() instanceof ServerPlayer player)
          PacketDistributor.sendToPlayer(player, new SOpenFilePacket(new File("logs/modular_machinery_reborn/mmr.log").toURI().toString()));
        return 1;
      });
  }

  private static ArgumentBuilder<CommandSourceStack, ?> reload() {
    return Commands.literal("reload")
      .requires(cs -> cs.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
      .executes(ctx -> {
        Config.load();
        ConfigLoaded.load();
        EnergyDisplayUtil.loadFromConfig();
        if (ctx.getSource().getEntity() instanceof ServerPlayer player) {
          reloadMachines(ctx.getSource().getServer(), player);
          ModularMachineryReborn.CONTROLLERS.forEach(controller -> {
            if (controller.getStatus().isMissingStructure()) return;
            controller.getProcessor().reset();
            controller.getProcessor().cores().forEach(MachineProcessorCore::reload);
          });
        }
        return 1;
      });
  }

  public static void reloadMachines(MinecraftServer server, @Nullable ServerPlayer player) {
    var state = new PreparableReloadListener.SharedState(server.getResourceManager());

    var listener = new MachineJsonReloadListener();
    listener.prepareSharedState(state);
    listener.reload(state, Util.backgroundExecutor(), CompletableFuture::completedFuture, server)
      .thenRun(() -> {
        if (player != null)
          player.sendSystemMessage(Component.translatable(ModularMachineryReborn.MODID + ".command.reload.machines").withStyle(ChatFormatting.GRAY));
      })
      .thenRun(() ->
          ModularMachineryReborn.CONTROLLERS.forEach(MachineControllerEntity::onStructureUnformed)
      );
  }
}
