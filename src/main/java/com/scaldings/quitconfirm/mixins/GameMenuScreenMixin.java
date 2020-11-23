package com.scaldings.quitconfirm.mixins;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.realms.gui.screen.RealmsBridgeScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen
{
    private CheckboxWidget checkbox;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    /**
     * @author scaldings
     */
    @Overwrite
    public void initWidgets()
    {
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TranslatableText("menu.returnToGame"), (buttonWidgetx) -> {
            this.client.openScreen((Screen)null);
            this.client.mouse.lockCursor();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, new TranslatableText("gui.advancements"), (buttonWidgetx) -> {
            this.client.openScreen(new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler()));
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, new TranslatableText("gui.stats"), (buttonWidgetx) -> {
            this.client.openScreen(new StatsScreen(this, this.client.player.getStatHandler()));
        }));
        String string = SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, new TranslatableText("menu.sendFeedback"), (buttonWidgetx) -> {
            this.client.openScreen(new ConfirmChatLinkScreen((bl) -> {
                if (bl) {
                    Util.getOperatingSystem().open(string);
                }

                this.client.openScreen(this);
            }, string, true));
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, new TranslatableText("menu.reportBugs"), (buttonWidgetx) -> {
            this.client.openScreen(new ConfirmChatLinkScreen((bl) -> {
                if (bl) {
                    Util.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
                }

                this.client.openScreen(this);
            }, "https://aka.ms/snapshotbugs?ref=game", true));
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, new TranslatableText("menu.options"), (buttonWidgetx) -> {
            this.client.openScreen(new OptionsScreen(this, this.client.options));
        }));
        ButtonWidget buttonWidget = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, new TranslatableText("menu.shareToLan"), (buttonWidgetx) -> {
            this.client.openScreen(new OpenToLanScreen(this));
        }));
        buttonWidget.active = this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote();
        ButtonWidget buttonWidget2 = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, new TranslatableText("menu.returnToMenu"), (buttonWidgetx) -> {
            if (checkbox.isChecked())
            {
                boolean bl = this.client.isInSingleplayer();
                boolean bl2 = this.client.isConnectedToRealms();
                buttonWidgetx.active = false;
                this.client.world.disconnect();
                if (bl) {
                    this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
                } else {
                    this.client.disconnect();
                }

                if (bl) {
                    this.client.openScreen(new TitleScreen());
                } else if (bl2) {
                    RealmsBridgeScreen realmsBridgeScreen = new RealmsBridgeScreen();
                    realmsBridgeScreen.switchToRealms(new TitleScreen());
                } else {
                    this.client.openScreen(new MultiplayerScreen(new TitleScreen()));
                }
            }
        }));
        this.checkbox = new CheckboxWidget(this.width / 2 - 102 + 205, this.height / 4 + 120 + -16, 150, 20, Text.of(""), false);
        this.addButton(this.checkbox);
        if (!this.client.isInSingleplayer()) {
            buttonWidget2.setMessage(new TranslatableText("menu.disconnect"));
        }
    }
}