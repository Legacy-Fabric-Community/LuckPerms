/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.fabric.mixin;

import me.lucko.luckperms.fabric.event.PlayerLoginCallback;
import me.lucko.luckperms.fabric.event.RespawnPlayerCallback;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(at = @At("HEAD"), method = "onPlayerConnect")
    private void luckperms_onLogin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerLoginCallback.EVENT.invoker().onLogin(player);
    }

    @Inject(at = @At("TAIL"), method = "respawnPlayer", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void luckperms_onRespawnPlayer(ServerPlayerEntity player, int dimension, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        RespawnPlayerCallback.EVENT.invoker().onRespawn(player, cir.getReturnValue(), server.getWorld(dimension), alive); // Transfer the old caches to the new player.
    }
}
