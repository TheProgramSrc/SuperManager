package xyz.theprogramsrc.supermanager;

import xyz.theprogramsrc.supercoreapi.global.translations.Translation;
import xyz.theprogramsrc.supercoreapi.global.translations.TranslationManager;
import xyz.theprogramsrc.supercoreapi.global.translations.TranslationPack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum L implements TranslationPack {

    /* Texts */
    DESCRIPTION("Description"),
    AUTHOR("Author"),
    SUPPORTED_VERSIONS("Supported Versions"),
    UNKNOWN_VERSION("Unknown version"),
    UNKNOWN_WORLD("Unknown World"),

    /* Messages */
    MODULE_DISABLED("&cThe module &9{ModuleName}&c is disabled! To use this feature you need to enable it"),
    NO_ACCESS_TO_MODULE("&cYou don't have access to the module &9{ModuleName}&c! You need the permission &b{ModulePermission} &cto use it."),
    NO_CONNECTION("&cThere is no internet connection. Please try later"),
    ERROR_WHILE_FETCHING_LATEST_VERSION("&cAn unknown error occurred while trying to fetch the latest version of &7{PluginName}&c. Please try again later."),
    PLUGIN_UP_TO_DATE("&aThe plugin &7{PluginName}&a is up to date!"),
    PLUGIN_NEEDS_UPDATE("&cThe plugin &7{PluginName}&c needs to be updated from the version &7{CurrentVersion}&c to the version &a{LatestVersion}"),
    DOWNLOADING_UPDATE("&aThe update download for &7{PluginName}&a has been started."),
    ERROR_ON_DOWNLOAD("&cError while downloading the update for &7{PluginName}&c. You'll find more information in the console."),
    SUCCESS_DOWNLOAD("&aThe download for the plugin &7{PluginName}&a has been successfully finished. Please restart the server to apply the changes."),
    TOKEN_WILL_NOT_BE_SHARED("&aDon't worry, your token will only be available in the config file and we won't share it to anyone."),
    TOKEN_SAVED("&aThe token was saved in the config."),
    MODULE_STATUS_UPDATED("&aThe new status of the module &7{ModuleName} &ais: &7{ModuleStatus}"),
    ERROR_ON_PRODUCT_DOWNLOAD("&cError while downloading the product &7{ProductName}&c (&7{ProductVersion}&c). You'll find more information in the console."),
    PRODUCT_VERSION_URL("&aClick the following link to see the product version in your browser: &7{ProductURL}"),
    PRODUCT_DOWNLOAD_IS_URL("&cThe product has an url as download and we cannot verify the source. For your security we will not download this file. You will need to download it manually."),
    PLUGIN_MANAGER_BROWSER_CACHE_RELOAD_REQUEST_SENT("&aCache reload request sent."),
    PLUGIN_MANAGER_PLUGIN_ALREADY_ENABLED("&cThe plugin &7{PluginName} &cis already enabled."),
    PLUGIN_MANAGER_PLUGIN_ALREADY_DISABLED("&cThe plugin &7{PluginName} &cis already disabled."),
    PLUGIN_MANAGER_PLUGIN_FAILED_TO_ENABLE("&cFailed to enable &7{PluginName}&c."),
    PLUGIN_MANAGER_PLUGIN_FAILED_TO_DISABLE("&cFailed to disable &7{PluginName}&c."),
    PLUGIN_MANAGER_PLUGIN_ENABLED("&aSuccessfully enabled &7{PluginName}&a."),
    PLUGIN_MANAGER_PLUGIN_DISABLED("&aSuccessfully disabled &7{PluginName}&a."),
    PLUGIN_MANAGER_CHECKING_FOR_UPDATES("&aChecking for &7{PluginName}&a updates..."),
    PLUGIN_MANAGER_FAILED_TO_CHECK_FOR_UPDATES("&cFailed to check for &7{PluginName}&c updates."),
    PLUGIN_MANAGER_NEW_UPDATE_AVAILABLE("&aA new update (&7{NewVersion}&a)is available for &7{PluginName}&a!"),
    PLUGIN_MANAGER_ALREADY_UP_TO_DATE("&aPlugin already up to date!"),
    PLUGIN_MARKETPLACE_STILL_LOADING("&cThe Plugin marketplace module is still loading products, you might not find every product that is currently available in the marketplace."),
    PLUGIN_MARKETPLACE_DOWNLOADING_PRODUCT("&aThe product download &7{ProductName}&a has been started."),
    PLUGIN_MARKETPLACE_CANNOT_DOWNLOAD_PAID_PLUGIN("&cCurrently you can't download paid products. This feature is being built."),
    PLUGIN_MARKETPLACE_MESSAGE_RESPONSE("&cReceived the following response from the website: &7{Message}"),
    PLUGIN_MARKETPLACE_INSTALLING_PLUGIN("&aInstalling plugin &7{PluginName}&a..."),
    PLUGIN_MARKETPLACE_PLUGIN_ENABLED("&aThe plugin &7{PluginName}&a was successfully enabled!"),
    PLUGIN_MARKETPLACE_PLUGIN_ENABLE_WARNING("&6WARNING: IS RECOMMENDED TO RESTART THE SERVER TO AVOID BUGS BECAUSE THE PLUGIN ENABLING BY &9SuperManager &6IS NOT AN OFFICIAL WAY TO ENABLE A PLUGIN"),
    PLUGIN_MARKETPLACE_FAILED_TO_ENABLE_PLUGIN("&cError while enabling the plugin &7{PluginName}&c. You will find more info in the console."),
    PLUGIN_MARKETPLACE_PLUGIN_ALREADY_INSTALLED("&cThe plugin &7{PluginName}&c is already installed!"),
    PLUGIN_MARKETPLACE_FAILED_PLUGIN_DOWNLOAD("&cAn unknown error prevented SuperManager from downloading the plugin &a{PluginName}&c."),
    PLUGIN_MARKETPLACE_INVALID_FILE("&cThe download file is not a jar file, you need to install it manually. You will find the download in the path &7{Path}"),
    USER_MANAGER_FREEZE_STATUS("&7{UserName}'s&a frozen status: &r{Status}"),
    USER_MANAGER_TELEPORTING("&aTeleporting to &7{UserName}'s&7 location..."),
    USER_MANAGER_OFFLINE_USER("&cThe user &7{UserName}&c is currently offline. Please try again later"),

    /* Dialogs */

    DIALOG_TOKEN_INPUT_TITLE("&9Token"),
    DIALOG_TOKEN_INPUT_SUBTITLE("&7Type in your Songoda token (You can create one here: http://songoda.com/account/api-tokens)"),
    DIALOG_TOKEN_INPUT_ACTIONBAR("&aThis will let the plugin download premium products."),

    /* Modules */
    PLUGIN_MANAGER_DISPLAY("Plugin Manager"),
    PLUGIN_MANAGER_NAME("&aPlugin Manager"),
    PLUGIN_MANAGER_LORE("&7Manage your Plugins in-game"),

    /* Module Browser */
    MODULE_MANAGER_DISABLE_ACTION("Disable Module"),
    MODULE_MANAGER_ENABLE_ACTION("Enable Module"),
    MODULE_MANAGER_FILTER_ENABLED_NAME("&aOnly Enabled"),
    MODULE_MANAGER_FILTER_ENABLED_LORE("&7Only show the&a enabled &7modules."),
    MODULE_MANAGER_FILTER_ALL_NAME("&aAll modules"),
    MODULE_MANAGER_FILTER_ALL_LORE("&7Show&a all&7 the modules."),

    /* Plugin Manager */
    PLUGIN_MANAGER_BROWSER_TITLE("&aPlugins"),
    PLUGIN_MANAGER_BROWSER_ITEM_NAME("&a{PluginName}"),
    PLUGIN_MANAGER_BROWSER_ITEM_LORE("&7Manage plugin &a{PluginName} &7(&c{PluginVersion}&7)"),

    PLUGIN_MANAGER_BROWSER_REFRESH_CACHE_NAME("&aRefresh Cache"),
    PLUGIN_MANAGER_BROWSER_REFRESH_CACHE_LORE("&7Click to refresh the cache"),

    PLUGIN_VIEW_TITLE("&aPlugins&7 > &c{PluginName}"),

    PLUGIN_VIEW_ENABLE_NAME("&aEnable Plugin"),
    PLUGIN_VIEW_ENABLE_LORE("&7Click to&a enable&7 the plugin"),
    PLUGIN_VIEW_DISABLE_NAME("&cDisable Plugin"),
    PLUGIN_VIEW_DISABLE_LORE("&7Click to&c disable&7 the plugin"),
    PLUGIN_MANAGER_NOT_RECOMMENDED_ACTION("&cNOT RECOMMENDED, MAY CRASH OTHER PLUGINS."),

    PLUGIN_VIEW_CHECK_UPDATE_ITEM_NAME("&aCheck for Updates"),
    PLUGIN_VIEW_CHECK_UPDATE_ITEM_LORE("&7Click to check for updates."),

    PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_NAME("&aDownload Update"),
    PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_LORE("&7Click to download the latest update for &a{PluginName}"),

    /* Plugin Marketplace */
    PLUGIN_MARKETPLACE_DISPLAY("Songoda Marketplace"),
    PLUGIN_MARKETPLACE_NAME("&cSongoda Marketplace"),
    PLUGIN_MARKETPLACE_LORE("&7The &cSongoda Marketplace&7 but in-game!"),

    PLUGIN_MARKETPLACE_TITLE("&aPlugin Marketplace"),
    PLUGIN_MARKETPLACE_LEFT_ACTION("Show product URL"),
    PLUGIN_MARKETPLACE_RIGHT_ACTION("Download and Install plugin"),
    PLUGIN_MARKETPLACE_CARD_NAME("&a{ProductName} - &7{ProductTagline}"),
    PLUGIN_MARKETPLACE_CARD_AUTHOR("&aProduct author: &c{ProductAuthor}"),
    PLUGIN_MARKETPLACE_CARD_SUPPORTED_VERSIONS("&aSupported Versions: &6{SupportedVersions}"),
    PLUGIN_MARKETPLACE_CARD_PRICE("&aProduct Price: &e{ProductPrice}"),

    /* User Manager */
    USER_MANAGER_BROWSER_TITLE("&aUser Manager"),
    USER_MANAGER_BROWSER_ITEM_NAME("&a{UserName}"),
    USER_MANAGER_BROWSER_ITEM_LORE("&7Click to manage &a{UserName}"),

    USER_MANAGER_EDITOR_TITLE("&aUsers &8> &9{UserName}"),
    USER_MANAGER_EDITOR_INFORMATION_NAME("&aUser Information:"),
    USER_MANAGER_EDITOR_INFORMATION_POSITION("&7Position: &aX: &9{POS_X}&a, Y: &9{POS_Y}&a, Z: &9{POS_Z}&a, World: &9{World}"),
    USER_MANAGER_EDITOR_INFORMATION_HEALTH("&7Health: &c{HealthLevel}"),
    USER_MANAGER_EDITOR_INFORMATION_FOOD("&7Health: &c{FoodLevel}"),

    USER_MANAGER_EDITOR_TELEPORT_NAME("&aTeleport"),
    USER_MANAGER_EDITOR_TELEPORT_LORE("&7Click to teleport to &a{UserName}'s&7 location."),

    USER_MANAGER_EDITOR_FREEZE_NAME("&aFreeze"),
    USER_MANAGER_EDITOR_FREEZE_LORE("&7Click to freeze &a{UserName}&7."),

    USER_MANAGER_EDITOR_UNFREEZE_NAME("&aUnFreeze"),
    USER_MANAGER_EDITOR_UNFREEZE_LORE("&7Click to unfreeze &a{UserName}&7."),
    ;

    private TranslationManager translationManager;
    private final String value;

    L(String value){
        this.value = value;
    }

    @Override
    public Locale getLanguage() {
        return new Locale("en","US");
    }

    @Override
    public Translation get() {
        return new Translation(this, name(), value);
    }

    @Override
    public List<Translation> translations() {
        return Arrays.stream(values()).map(L::get).collect(Collectors.toList());
    }

    @Override
    public void setManager(TranslationManager manager) {
        this.translationManager = manager;
    }

    @Override
    public TranslationManager getManager() {
        return this.translationManager;
    }

    @Override
    public String toString() {
        return this.get().translate();
    }
}
