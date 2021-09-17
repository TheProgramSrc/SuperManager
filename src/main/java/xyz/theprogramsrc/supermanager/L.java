package xyz.theprogramsrc.supermanager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import xyz.theprogramsrc.supercoreapi.global.translations.Translation;
import xyz.theprogramsrc.supercoreapi.global.translations.TranslationManager;
import xyz.theprogramsrc.supercoreapi.global.translations.TranslationPack;

public enum L implements TranslationPack {

    /* Texts */
    DESCRIPTION("General.Description","Description"),
    AUTHOR("General.Author","Author"),
    SUPPORTED_VERSIONS("General.SupportedVersions","Supported Versions"),
    UNKNOWN_VERSION("General.UnknownVersion","Unknown version"),
    UNKNOWN_WORLD("General.UnknownWorld","Unknown World"),
    JOIN("General.Join","Join"),
    WRITE("General.Write","Write"),
    CHANNELS("General.Channels","Channels"),
    PERMISSIONS("General.Permissions","Permissions"),
    FREE("General.Free", "Free"),
    SONGODA_PLUS("General.SongodaPlus", "Included with Songoda+"),

    /* Messages */
    FAILED_TO_RETRIEVE_MODULE("Errors.FailedToRetrieveModule","&cFailed to retrieve module data. Please try again later."),
    MODULE_STATUS_CHANGED("Messages.ModuleStatusUpdated","&7The new status for the module &c{ModuleName}&7is: &r{Status}"),
    MODULE_ALREADY_ENABLED("Errors.ModuleAlreadyEnabled","&cThe module &7{ModuleName}&c is already enabled!"),
    MODULE_ALREADY_DISABLED("Errors.ModuleAlreadyDisabled","&cThe module &7{ModuleName}&c is already disabled!"),
    MODULE_DISABLED("Messages.ModuleDisabled","&cThe module &9{ModuleName}&c is disabled! To use this feature you need to enable it"),
    NO_ACCESS_TO_MODULE("Messages.NoAccessToModule","&cYou don't have access to the module &9{ModuleName}&c! You need the permission &b{ModulePermission} &cto use it."),
    NO_CONNECTION("Messages.NoConnection","&cThere is no internet connection. Please try later"),
    TOKEN_WILL_NOT_BE_SHARED("Messages.TokenWillNotBeShared","&aDon't worry, your token will only be available in the config file and we won't share it to anyone."),
    TOKEN_SAVED("Messages.TokenSaved","&aThe token was saved in the config."),
    INVALID_TIME_FORMAT("Messages.InvalidTimeFormat","&cThe time format is invalid."),

    /* Plugin Manager - Main */
    PLUGIN_MANAGER_BROWSER_CACHE_RELOAD_REQUEST_SENT("Modules.PluginManager.ReloadCache", "&aCache reload request sent."),
    PLUGIN_MANAGER_PLUGIN_ALREADY_ENABLED("Modules.PluginManager.PluginAlreadyEnabled","&cThe plugin &7{PluginName} &cis already enabled."),
    PLUGIN_MANAGER_PLUGIN_ALREADY_DISABLED("Modules.PluginManager.PluginAlreadyDisabled","&cThe plugin &7{PluginName} &cis already disabled."),
    PLUGIN_MANAGER_PLUGIN_FAILED_TO_ENABLE("Modules.PluginManager.FailedToEnablePlugin","&cFailed to enable &7{PluginName}&c."),
    PLUGIN_MANAGER_PLUGIN_FAILED_TO_DISABLE("Modules.PluginManager.FailedToDisablePlugin","&cFailed to disable &7{PluginName}&c."),
    PLUGIN_MANAGER_PLUGIN_ENABLED("Modules.PluginManager.PluginEnabled","&aSuccessfully enabled &7{PluginName}&a."),
    PLUGIN_MANAGER_PLUGIN_DISABLED("Modules.PluginManager.PluginDisabled","&aSuccessfully disabled &7{PluginName}&a."),

    /* Plugin Manager - Updater */
    PLUGIN_MANAGER_CHECKING_FOR_UPDATES("Modules.PluginManager.CheckingForUpdates","&aChecking for &7{PluginName}&a updates..."),
    PLUGIN_MANAGER_FAILED_TO_CHECK_FOR_UPDATES("Modules.PluginManager.UpdateCheckFailed","&cFailed to check for &7{PluginName}&c updates."),
    PLUGIN_MANAGER_NEW_UPDATE_AVAILABLE("Modules.PluginManager.UpdateAvailable","&aA new update (&7{NewVersion}&a) is available for &7{PluginName}&a!"),
    PLUGIN_MANAGER_ALREADY_UP_TO_DATE("Modules.PluginManager.UpToDate","&aPlugin already up to date!"),
    PLUGIN_MANAGER_DOWNLOADING_UPDATE("Modules.PluginUpdater.DownloadingUpdate","&aThe update download for &7{PluginName}&a has been started."),
    PLUGIN_MANAGER_SUCCESS_DOWNLOAD("Modules.PluginUpdater.SuccessDownload","&aThe download for the plugin &7{PluginName}&a has been successfully finished. Please restart the server to apply the changes."),
    PLUGIN_MANAGER_ERROR_ON_DOWNLOAD("Modules.PluginUpdater.FailedDownload","&cError while downloading the update for &7{PluginName}&c. You'll find more information in the console."),

    /* Plugin Marketplace - Main */
    PLUGIN_MARKETPLACE_STILL_LOADING("Modules.Marketplace.StillLoading","&cThe Plugin marketplace module is still loading products, you might not find every product that is currently available in the marketplace."),
    PLUGIN_MARKETPLACE_DOWNLOADING_PRODUCT("Modules.Marketplace.DownloadingProduct","&aThe product download &7{ProductName}&a has been started."),
    PLUGIN_MARKETPLACE_CANNOT_DOWNLOAD_PAID_PLUGIN("Modules.Marketplace.CannotDownloadPaidProducts","&cCurrently you can't download paid products. If you want to know how to do it check the wiki: https://wiki.theprogramsrc.xyz/SuperManager"),
    PLUGIN_MARKETPLACE_MESSAGE_RESPONSE("Modules.Marketplace.UnknownResponse","&cReceived the following response from the website: &7{Message}"),
    PLUGIN_MARKETPLACE_INSTALLING_PLUGIN("Modules.Marketplace.InstallingPlugin","&aInstalling plugin &7{PluginName}&a..."),
    PLUGIN_MARKETPLACE_PLUGIN_ENABLED("Modules.Marketplace.PluginEnabled","&aThe plugin &7{PluginName}&a was successfully enabled!"),
    PLUGIN_MARKETPLACE_PLUGIN_ENABLE_WARNING("Modules.Marketplace.RestartWarning","&6WARNING: IS RECOMMENDED TO RESTART THE SERVER TO AVOID BUGS BECAUSE THE PLUGIN ENABLING BY &9SuperManager &6IS NOT AN OFFICIAL WAY TO ENABLE A PLUGIN"),
    PLUGIN_MARKETPLACE_FAILED_TO_ENABLE_PLUGIN("Modules.Marketplace.FailedToEnablePlugin","&cError while enabling the plugin &7{PluginName}&c. You will find more info in the console."),
    PLUGIN_MARKETPLACE_PLUGIN_ALREADY_INSTALLED("Modules.Marketplace.PluginAlreadyInstalled","&cThe plugin &7{PluginName}&c is already installed!"),
    PLUGIN_MARKETPLACE_FAILED_PLUGIN_DOWNLOAD("Modules.Marketplace.FailedDownload","&cAn unknown error prevented SuperManager from downloading the plugin &a{PluginName}&c."),
    PLUGIN_MARKETPLACE_INVALID_FILE("Modules.Marketplace.InvalidFile","&cThe download file is not a jar file, you need to install it manually. You will find the download in the path &7{Path}"),

    /* User Manager - Actions */
    USER_MANAGER_FREEZE_STATUS("Modules.UserManager.FrozenStatus","&a{UserName}'s&7 frozen status: &r{Status}"),
    USER_MANAGER_TELEPORTING("Modules.UserManager.Teleporting","&7Teleporting to &a{UserName}'s&7 location..."),
    USER_MANAGER_OFFLINE_USER("Modules.UserManager.OfflineUser","&cThe user &7{UserName}&c is currently offline. Please try again later"),
    USER_MANAGER_MESSAGE_SENT("Modules.UserManager.MessageSent","&7The message to &a{UserName}&7 was successfully sent. Here is a preview:"),

    /* Chat Channels */
    CHAT_CHANNELS_ALREADY_EXISTS("Modules.ChatChannels.AlreadyExists","&7A channel with that name already exists!"),
    CHAT_CHANNELS_DOESNT_EXISTS("Modules.ChatChannels.DoesntExists","&cThe channel &7{ChannelName}&c doesn't exists!"),
    CHAT_CHANNELS_CONTAINS_SPACES("Modules.ChatChannels.ContainsSpaces","&7Channel should not contain spaces!"),
    CHAT_CHANNELS_HELP_JOIN("Modules.ChatChannels.Help.Join","&7To join a specific channel use the command &e/{Command} join <ChannelName>&7."),
    CHAT_CHANNELS_HELP_LIST("Modules.ChatChannels.Help.List","&7To get the list of the available channels use the command &e/{Command} list"),
    CHAT_CHANNELS_HELP_PERMISSIONS("Modules.ChatChannels.Help.Permissions","&7To get the usage of the permissions use the command &e/{Command} permissions"),
    CHAT_CHANNELS_LIST_ITEM("Modules.ChatChannels.List.Item","&7- &8#{ChannelName}"),
    CHAT_CHANNELS_JOINED("Modules.ChatChannels.Joined","&7Now you're in the channel &a{ChannelName}"),
    CHAT_CHANNELS_FULL("Modules.ChatChannels.Full","&cThe channel is full! Please try again later"),
    CHAT_CHANNELS_ONLINE("Modules.ChatChannels.Online","&aThere are currently &e{Online}&7/&c{Max}&a players in this channel."),

    /* File Manager */
    FILE_MANAGER_PROTECTED_FILE("Modules.FileManager.Protected","&cThe file &7{FileName}&c is protected and you can't delete it."),
    FILE_MANAGER_FAILED_TO_DELETE_DIRECTORY("Modules.FileManager.FailedDirectoryDeletion","&cFailed to delete the directory &7{Path}&c."),
    FILE_MANAGER_INVALID_NUMBER("Modules.FileManager.InvalidNumber","&cThe provided input is not a valid number!"),
    FILE_MANAGER_REMOVE_FROM_LIST("Modules.FileManager.RemoveFromList","&aTo remove the line from the list type &7yml-editor:line-remove&a."),

    WORLD_MANAGER_BACKUP_CREATING("Modules.WorldManager.CreatingBackup","&aCreating backup, please wait..."),
    WORLD_MANAGER_BACKUP_FAILED("Modules.WorldManager.BackupCreationFailed","&cFailed to create backup. You'll find more info in the console"),
    WORLD_MANAGER_BACKUP_SUCCESS("Modules.WorldManager.BackupCreated","&aSuccessfully created and saved the backup to the path &b{Path}&a."),

    /* BackupManager */
    BACKUP_MANAGER_AVAILABLE_TIME_UNITS("Modules.BackupManager.TimeUnits.Available","&7Available time units: &6&l(SEPARATE THEM USING A SPACE ' ')"),
    BACKUP_MANAGER_AVAILABLE_TIME_UNITS_SECOND("Modules.BackupManager.TimeUnits.Second", "&7- &c1s -> 1 Second"),
    BACKUP_MANAGER_AVAILABLE_TIME_UNITS_MINUTE("Modules.BackupManager.TimeUnits.Minute", "&7- &c1m -> 1 Minute"),
    BACKUP_MANAGER_AVAILABLE_TIME_UNITS_HOUR("Modules.BackupManager.TimeUnits.Hour", "&7- &c1h -> 1 Hour"),
    BACKUP_MANAGER_AVAILABLE_TIME_UNITS_DAY("Modules.BackupManager.TimeUnits.Day", "&7- &c1d -> 1 Day"),
    BACKUP_MANAGER_AVAILABLE_TIME_UNITS_EXAMPLE("Modules.BackupManager.TimeUnits.Example","&7Example: &6&l1s 4m 6h 4d"),
    BACKUP_MANAGER_CREATING_BACKUP("Modules.BackupManager.CreatingBackup","&aCreating backup, please wait..."),
    BACKUP_MANAGER_BACKUP_CREATED("Modules.BackupManager.BackupCreated","&aSuccessfully created and saved the backup! Now we will start the backup process. Please wait..."),
    BACKUP_MANAGER_FAILED_TO_BACKUP_DATA("Modules.BackupManager.FailedToBackupData","&cFailed to backup the data! Please check the console for more info."),
    BACKUP_MANAGER_SUCCESSFULLY_BACKED_UP_DATA("Modules.BackupManager.SuccessfullyBackedUpData","&aSuccessfully backed up &c{FilesAmount}&a files! The next backup will be created at &b{NextBackupAt}&a."),
    BACKUP_MANAGER_BACKUP_IN_PROCESS("Modules.BackupManager.BackupInProcess","Backup in Process..."),
    BACKUP_MANAGER_BACKUP_DELETED("Modules.BackupManager.BackupDeleted", "Backup deleted"),

    /* Dialogs */

    DIALOG_TOKEN_INPUT_TITLE("Dialogs.EditToken.Title","&9Token"),
    DIALOG_TOKEN_INPUT_SUBTITLE("Dialogs.EditToken.Subtitle","&7Type in your Songoda token (You can create one here: http://songoda.com/account/api-tokens)"),
    DIALOG_TOKEN_INPUT_ACTIONBAR("Dialogs.EditToken.Actionbar","&aThis will let the plugin download premium products."),

    USER_MANAGER_EDITOR_SEND_MESSAGE_DIALOG_TITLE("Dialogs.UserManager.SendMessage.Title","&9Send Message"),
    USER_MANAGER_EDITOR_SEND_MESSAGE_DIALOG_SUBTITLE("Dialogs.UserManager.SendMessage.Subtitle","&7Send a message to &a{UserName}"),
    USER_MANAGER_EDITOR_SEND_MESSAGE_DIALOG_ACTIONBAR("Dialogs.UserManager.SendMessage.Actionbar","&aWrite a message in the chat to send to &7{UserName}"),

    CHAT_CHANNELS_CREATOR_DIALOG_TITLE("Dialogs.ChatChannels.CreateChatChannel.Title","&9Create Chat Channel"),
    CHAT_CHANNELS_CREATOR_DIALOG_SUBTITLE("Dialogs.ChatChannels.CreateChatChannel.Subtitle","&7Write the channel name"),
    CHAT_CHANNELS_CREATOR_DIALOG_ACTIONBAR("Dialogs.ChatChannels.CreateChatChannel.Actionbar","&aThis will be the name of the chat channel."),

    CHAT_CHANNELS_UPDATE_FORMAT_DIALOG_TITLE("Dialogs.ChatChannels.UpdateChatFormat.Title","&9Update Format"),
    CHAT_CHANNELS_UPDATE_FORMAT_DIALOG_SUBTITLE("Dialogs.ChatChannels.UpdateChatFormat.Subtitle","&7Write the new chat format"),
    CHAT_CHANNELS_UPDATE_FORMAT_DIALOG_ACTIONBAR("Dialogs.ChatChannels.UpdateChatFormat.Actionbar","&7Current format: &r{ChatFormat}"),

    FILE_MANAGER_SET_STRING_TITLE("Dialogs.FileManager.SetString.Title","&9Set String"),
    FILE_MANAGER_SET_STRING_SUBTITLE("Dialogs.FileManager.SetString.Subtitle","&7Write the new value"),
    FILE_MANAGER_SET_STRING_ACTIONBAR("Dialogs.FileManager.SetString.Actionbar","&aCurrent Value: &r{CurrentValue}"),

    FILE_MANAGER_SET_NUMBER_TITLE("Dialogs.FileManager.SetNumber.Title","&9Set Number"),
    FILE_MANAGER_SET_NUMBER_SUBTITLE("Dialogs.FileManager.SetNumber.Subtitle","&7Write the new number"),
    FILE_MANAGER_SET_NUMBER_ACTIONBAR("Dialogs.FileManager.SetNumber.Actionbar","&aCurrent Value: &r{CurrentValue}"),

    FILE_MANAGER_ADD_TO_LIST_TITLE("Dialogs.FileManager.AddToList.Title","&9Add to list"),
    FILE_MANAGER_ADD_TO_LIST_SUBTITLE("Dialogs.FileManager.AddToList.Subtitle","&7Write the new value"),
    FILE_MANAGER_ADD_TO_LIST_ACTIONBAR("Dialogs.FileManager.AddToList.Actionbar","&aThe new value will be added to the list."),

    FILE_MANAGER_SET_TO_LIST_TITLE("Dialogs.FileManager.SetToList.Title","&9Set to list"),
    FILE_MANAGER_SET_TO_LIST_SUBTITLE("Dialogs.FileManager.SetToList.Subtitle","&7Write the new value"),
    FILE_MANAGER_SET_TO_LIST_ACTIONBAR("Dialogs.FileManager.SetToList.Actionbar","&aCurrent value in list: &r{CurrentValue}"),

    BACKUP_MANAGER_SCHEDULE_BACKUP_TITLE("Dialogs.BackupManager.ScheduleBackup.Title", "&eSchedule Backup"),
    BACKUP_MANAGER_SCHEDULE_BACKUP_SUBTITLE("Dialogs.BackupManager.ScheduleBackup.Subtitle", "&7Write the time between backups"),
    BACKUP_MANAGER_SCHEDULE_BACKUP_ACTIONBAR("Dialogs.BackupManager.ScheduleBackup.Actionbar", "&cMake sure to use the given format"),

    BACKUP_MANAGER_RENAME_BACKUP_TITLE("Dialogs.BackupManager.RenameBackup.Title", "&bRename Backup"),
    BACKUP_MANAGER_RENAME_BACKUP_SUBTITLE("Dialogs.BackupManager.RenameBackup.Subtitle", "&7Write the new backup name"),
    BACKUP_MANAGER_RENAME_BACKUP_ACTIONBAR("Dialogs.BackupManager.RenameBackup.Actionbar", "&cCurrent Name: &7{BackupName}"),

    /* Modules */

    /* Module Browser */
    MODULE_MANAGER_DISABLE_ACTION("GUI.ModuleManager.DisableModule","Disable Module"),
    MODULE_MANAGER_ENABLE_ACTION("GUI.ModuleManager.EnableModule","Enable Module"),
    MODULE_MANAGER_FILTER_ENABLED_NAME("GUI.ModuleManager.OnlyEnabled.Name","&aOnly Enabled"),
    MODULE_MANAGER_FILTER_ENABLED_LORE("GUI.ModuleManager.OnlyEnabled.Lore","&7Only show the&a enabled &7modules."),
    MODULE_MANAGER_FILTER_ALL_NAME("GUI.ModuleManager.AllModules.Name","&aAll modules"),
    MODULE_MANAGER_FILTER_ALL_LORE("GUI.ModuleManager.AllModules.Lore","&7Show&a all&7 the modules."),

    /* Plugin Manager */
    PLUGIN_MANAGER_DISPLAY("GUI.Modules.PluginManager.Display","Plugin Manager"),
    PLUGIN_MANAGER_NAME("GUI.Modules.PluginManager.Name","&aPlugin Manager"),
    PLUGIN_MANAGER_LORE("GUI.Modules.PluginManager.Lore","&7Manage your Plugins in-game"),
    PLUGIN_MANAGER_BROWSER_TITLE("GUI.Modules.PluginManager.Browser.Title","&aPlugins"),
    PLUGIN_MANAGER_BROWSER_ITEM_NAME("GUI.Modules.PluginManager.Browser.Item.Name","&a{PluginName}"),
    PLUGIN_MANAGER_BROWSER_ITEM_LORE("GUI.Modules.PluginManager.Browser.Item.Lore","&7Manage plugin &a{PluginName} &7(&c{PluginVersion}&7)"),

    PLUGIN_MANAGER_BROWSER_REFRESH_CACHE_NAME("GUI.Modules.PluginManager.Browser.RefreshCache.Name","&aRefresh Cache"),
    PLUGIN_MANAGER_BROWSER_REFRESH_CACHE_LORE("GUI.Modules.PluginManager.Browser.RefreshCache.Lore","&7Click to refresh the cache"),

    PLUGIN_VIEW_TITLE("GUI.Modules.PluginManager.View.Title","&aPlugins&7 > &c{PluginName}"),

    PLUGIN_VIEW_ENABLE_NAME("GUI.Modules.PluginManager.View.Enable.Name","&aEnable Plugin"),
    PLUGIN_VIEW_ENABLE_LORE("GUI.Modules.PluginManager.View.Enable.Lore","&7Click to&a enable&7 the plugin"),
    PLUGIN_VIEW_DISABLE_NAME("GUI.Modules.PluginManager.View.Disable.Name","&cDisable Plugin"),
    PLUGIN_VIEW_DISABLE_LORE("GUI.Modules.PluginManager.View.Disable.Lore","&7Click to&c disable&7 the plugin"),
    PLUGIN_MANAGER_NOT_RECOMMENDED_ACTION("GUI.Modules.PluginManager.View.Disable.Warning","&cNOT RECOMMENDED, MAY CRASH OTHER PLUGINS."),

    PLUGIN_VIEW_CHECK_UPDATE_ITEM_NAME("GUI.Modules.PluginManager.View.UpdateChecker.Name","&aCheck for Updates"),
    PLUGIN_VIEW_CHECK_UPDATE_ITEM_LORE("GUI.Modules.PluginManager.View.UpdateChecker.Lore","&7Click to check for updates."),

    PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_NAME("GUI.Modules.PluginManager.View.UpdateDownloader.Name","&aDownload Update"),
    PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_LORE("GUI.Modules.PluginManager.View.UpdateDownloader.Lore","&7Click to download the latest update for &a{PluginName}"),

    /* Plugin Marketplace */
    PLUGIN_MARKETPLACE_DISPLAY("GUI.Modules.Marketplace.Display","Songoda Marketplace"),
    PLUGIN_MARKETPLACE_NAME("GUI.Modules.Marketplace.Name","&cSongoda Marketplace"),
    PLUGIN_MARKETPLACE_LORE("GUI.Modules.Marketplace.Lore","&7The &cSongoda Marketplace&7 but in-game!"),

    PLUGIN_MARKETPLACE_TITLE("GUI.Modules.Marketplace.Browser.Title","&aPlugin Marketplace"),
    PLUGIN_MARKETPLACE_LEFT_ACTION("GUI.Modules.Marketplace.Browser.Item.ShowURL","Show product URL"),
    PLUGIN_MARKETPLACE_RIGHT_ACTION("GUI.Modules.Marketplace.Browser.Item.DownloadAndInstall","Download and Install plugin"),
    PLUGIN_MARKETPLACE_CARD_NAME("GUI.Modules.Marketplace.Browser.Card.Name","&a{ProductName} - &7{ProductTagline}"),
    PLUGIN_MARKETPLACE_CARD_NAME_NO_TAGLINE("GUI.Modules.Marketplace.Browser.Card.NameNoTagline","&a{ProductName}"),
    PLUGIN_MARKETPLACE_CARD_AUTHOR("GUI.Modules.Marketplace.Browser.Card.Author","&aProduct author: &c{ProductAuthor}"),
    PLUGIN_MARKETPLACE_CARD_SUPPORTED_VERSIONS("GUI.Modules.Marketplace.Browser.Card.SupportedVersions","&aSupported Versions: &6{SupportedVersions}"),
    PLUGIN_MARKETPLACE_CARD_PRICE("GUI.Modules.Marketplace.Browser.Card.Price","&aProduct Price: &e{ProductPrice}"),

    /* User Manager */
    USER_MANAGER_DISPLAY("GUI.Modules.UserManager.Display","User Manager"),
    USER_MANAGER_NAME("GUI.Modules.UserManager.Name","&aUser Manager"),
    USER_MANAGER_LORE("GUI.Modules.UserManager.Lore","&7Manage your users"),
    USER_MANAGER_BROWSER_TITLE("GUI.Modules.UserManager.Browser.Title","&aUser Manager"),
    USER_MANAGER_BROWSER_ITEM_NAME("GUI.Modules.UserManager.Browser.Item.Name","&a{UserName}"),
    USER_MANAGER_BROWSER_ITEM_LORE("GUI.Modules.UserManager.Browser.Item.Lore","&7Click to manage &a{UserName}"),
    USER_MANAGER_BROWSER_ONLINE_ONLY_NAME("GUI.Modules.UserManager.Browser.OnlineUsers.Name","&aToggle Online Only"),
    USER_MANAGER_BROWSER_ONLINE_ONLY_LORE("GUI.Modules.UserManager.Browser.OnlineUsers.Lore","&7Show online players only"),
    USER_MANAGER_BROWSER_ALL_ONLINE_NAME("GUI.Modules.UserManager.Browser.AllUsers.Name","&aShow All Players"),
    USER_MANAGER_BROWSER_ALL_ONLINE_LORE("GUI.Modules.UserManager.Browser.AllUsers.Lore","&7Show all the players"),

    USER_MANAGER_EDITOR_TITLE("GUI.Modules.UserManager.Editor.Title","&aUsers &8> &9{UserName}"),
    USER_MANAGER_EDITOR_INFORMATION_NAME("GUI.Modules.UserManager.Editor.Information.Name","&aUser Information:"),
    USER_MANAGER_EDITOR_INFORMATION_POSITION("GUI.Modules.UserManager.Information.Position","&7Position: &aX: &9{POS_X}&a, Y: &9{POS_Y}&a, Z: &9{POS_Z}&a, World: &9{World}"),
    USER_MANAGER_EDITOR_INFORMATION_HEALTH("GUI.Modules.UserManager.Editor.Information.Health","&7Health: &c{HealthLevel}"),
    USER_MANAGER_EDITOR_INFORMATION_FOOD("GUI.Modules.UserManager.Editor.Information.Food","&7Food: &c{FoodLevel}"),
    USER_MANAGER_EDITOR_TELEPORT_NAME("GUI.Modules.UserManager.Editor.Teleport.Name","&dTeleport"),
    USER_MANAGER_EDITOR_TELEPORT_LORE("GUI.Modules.UserManager.Editor.Teleport.Lore","&7Click to &dteleport&7 to &a{UserName}'s&7 location."),
    USER_MANAGER_EDITOR_FREEZE_NAME("GUI.Modules.UserManager.Editor.Freeze.Name","&b&lFreeze"),
    USER_MANAGER_EDITOR_FREEZE_LORE("GUI.Modules.UserManager.Editor.Freeze.Lore","&7Click to &b&lfreeze&r &a{UserName}&7."),
    USER_MANAGER_EDITOR_UNFREEZE_NAME("GUI.Modules.UserManager.Editor.UnFreeze.Name","&bUnFreeze"),
    USER_MANAGER_EDITOR_UNFREEZE_LORE("GUI.Modules.UserManager.Editor.UnFreeze.Lore","&7Click to &bunfreeze &a{UserName}&7."),
    USER_MANAGER_EDITOR_VIEW_INV_NAME("GUI.Modules.UserManager.Editor.Inventory.Name","&6Inventory"),
    USER_MANAGER_EDITOR_VIEW_INV_LORE("GUI.Modules.UserManager.Editor.Inventory.Lore","&7Click to open &a{UserName}'s &6inventory&7."),
    USER_MANAGER_EDITOR_VIEW_ENDER_CHEST_NAME("GUI.Modules.UserManager.Editor.EnderChest.Name","&5Ender Chest"),
    USER_MANAGER_EDITOR_VIEW_ENDER_CHEST_LORE("GUI.Modules.UserManager.Editor.EnderChest.Lore","&7Click to open &a{UserName}'s &5Ender Chest&7."),
    USER_MANAGER_EDITOR_SEND_MESSAGE_NAME("GUI.Modules.UserManager.Editor.SendMessage.Name","&aSend Message"),
    USER_MANAGER_EDITOR_SEND_MESSAGE_LORE("GUI.Modules.UserManager.Editor.SendMessage.Lore","&7Click to &aSend a Message&7 to &c{UserName}'s&7 chat."),

    /* Chat Channels */
    CHAT_CHANNELS_DISPLAY("GUI.Modules.ChatChannels.Display", "Chat Channels"),
    CHAT_CHANNELS_NAME("GUI.Modules.ChatChannels.Name","&cChat Channels"),
    CHAT_CHANNELS_LORE("GUI.Modules.ChatChannels.Lore","&7Create channels in your server!"),

    CHAT_CHANNELS_BROWSER_TITLE("GUI.Modules.ChatChannels.Browser.Title", "&cChat Channels"),
    CHAT_CHANNELS_BROWSER_ITEM_NAME("GUI.Modules.ChatChannels.Browser.Item.Name","&a#{ChannelName}"),
    CHAT_CHANNELS_BROWSER_ITEM_CREATED_AT("GUI.Modules.ChatChannels.Browser.Item.CreatedAt","&7Created At: &9{CreatedAt}"),
    CHAT_CHANNELS_BROWSER_ITEM_ID("GUI.Modules.ChatChannels.Browser.Item.ID","&7Channel Id: &9{ChannelId}"),
    CHAT_CHANNELS_BROWSER_ITEM_LEFT_ACTION("GUI.Modules.ChatChannels.Browser.Item.MarkAsGlobal","&7Click to mark as global channel"),
    CHAT_CHANNELS_BROWSER_ITEM_RIGHT_ACTION("GUI.Modules.ChatChannels.Browser.Item.Remove","&7Click to remove."),
    CHAT_CHANNELS_BROWSER_SETTINGS_NAME("GUI.Modules.ChatChannels.Browser.Settings.Name","&cSettings"),
    CHAT_CHANNELS_BROWSER_SETTINGS_LORE("GUI.Modules.ChatChannels.Browser.Settings.Lore","&7Click to edit the ChatChannels Settings"),

    CHAT_CHANNELS_SETTINGS_TITLE("GUI.Modules.ChatChannels.Settings.Title","&cChat Channels &7> &4Settings"),
    CHAT_CHANNELS_SETTINGS_CREATE_NAME("GUI.Modules.ChatChannels.Settings.Create.Name","&aCreate Channel"),
    CHAT_CHANNELS_SETTINGS_CREATE_LORE("GUI.Modules.ChatChannels.Settings.Create.Lore","&7Click to create a channel"),
    CHAT_CHANNELS_SETTINGS_UPDATE_FORMAT_NAME("GUI.Modules.ChatChannels.Settings.UpdateFormat.Name","&cUpdate Format"),
    CHAT_CHANNELS_SETTINGS_UPDATE_FORMAT_LORE("GUI.Modules.ChatChannels.Settings.UpdateFormat.Lore","&7Click to update the &cformat&7 of the chat."),
    CHAT_CHANNELS_SETTINGS_UPDATE_FORMAT_PREVIEW("GUI.Modules.ChatChannels.Settings.UpdateFormat.Preview","&7Current Format: &r{ChatFormat}"),

    /* File Manager */
    FILE_MANAGER_DISPLAY("GUI.Modules.FileManager.Display","File Manager"),
    FILE_MANAGER_NAME("GUI.Modules.FileManager.Name","&aFile Manager"),
    FILE_MANAGER_LORE("GUI.Modules.FileManager.Lore","&7Edit all your files in-game!"),

    FILE_MANAGER_FILE_BROWSER_TITLE("GUI.Modules.FileManager.Browser.Title","&aFile Browser &7> &9{Path}"),

    FILE_MANAGER_FILE_BROWSER_DIRECTORY_NAME("GUI.Modules.FileManager.Browser.Directory.Name","&6{FileName}"),
    FILE_MANAGER_FILE_BROWSER_DIRECTORY_LEFT("GUI.Modules.FileManager.Browser.Directory.Open","&7Open Directory"),
    FILE_MANAGER_FILE_BROWSER_DIRECTORY_RIGHT("GUI.Modules.FileManager.Browser.Directory.Delete","&cDelete Directory"),

    FILE_MANAGER_FILE_BROWSER_EDITABLE_NAME("GUI.Modules.FileManager.Browser.Editable.Name","&a{FileName}"),
    FILE_MANAGER_FILE_BROWSER_EDITABLE_LEFT("GUI.Modules.FileManager.Browser.Editable.Edit","&7Edit File"),
    FILE_MANAGER_FILE_BROWSER_EDITABLE_RIGHT("GUI.Modules.FileManager.Browser.Editable.Delete","&cDelete File"),

    FILE_MANAGER_FILE_BROWSER_NON_EDITABLE_NAME("GUI.Modules.FileManager.Browser.NonEditable.Name","&7{FileName}"),
    FILE_MANAGER_FILE_BROWSER_NON_EDITABLE_RIGHT("GUI.Modules.FileManager.Browser.NonEditable.Delete","&cDelete File"),

    FILE_MANAGER_YML_EDITOR_TITLE("GUI.Modules.FileManager.YMLEditor.Title","&aFileBrowser &7> &c{FileName}"),
    FILE_MANAGER_YML_EDITOR_ITEM_SECTION("GUI.Modules.FileManager.YMLEditor.Section","&7Current section: &a{Section}"),
    FILE_MANAGER_YML_EDITOR_ITEM_PREVIEW("GUI.Modules.FileManager.YMLEditor.Preview","&7Current value: &r{Preview}"),

    FILE_MANAGER_YML_EDITOR_STRING_NAME("GUI.Modules.FileManager.YMLEditor.String.Name","&aText"),
    FILE_MANAGER_YML_EDITOR_STRING_ACTION("GUI.Modules.FileManager.YMLEditor.String.Lore","&7Click to set the new value"),

    FILE_MANAGER_YML_EDITOR_NUMBER_NAME("GUI.Modules.FileManager.YMLEditor.Number.Name","&aNumber"),
    FILE_MANAGER_YML_EDITOR_NUMBER_LEFT("GUI.Modules.FileManager.YMLEditor.Number.Increase","&7Increase by &9{Amount}"),
    FILE_MANAGER_YML_EDITOR_NUMBER_Q("GUI.Modules.FileManager.YMLEditor.Number.WriteValue","&7Write a value"),
    FILE_MANAGER_YML_EDITOR_NUMBER_RIGHT("GUI.Modules.FileManager.YMLEditor.Number.Decrease","&7Decrease by &9{Amount}"),

    FILE_MANAGER_YML_EDITOR_BOOLEAN_NAME("GUI.Modules.FileManager.YMLEditor.Boolean.Name","&aBoolean"),
    FILE_MANAGER_YML_EDITOR_BOOLEAN_ACTION("GUI.Modules.FileManager.YMLEditor.Boolean.Toggle","&7Click to toggle"),

    FILE_MANAGER_YML_EDITOR_STRING_LIST_NAME("GUI.Modules.FileManager.YMLEditor.StringList.Name","&aString List"),
    FILE_MANAGER_YML_EDITOR_STRING_LIST_LEFT("GUI.Modules.FileManager.YMLEditor.StringList.AddValue","&7Add value to list"),
    FILE_MANAGER_YML_EDITOR_STRING_LIST_Q("GUI.Modules.FileManager.YMLEditor.StringList.MoveSelector","&7Change current line"),
    FILE_MANAGER_YML_EDITOR_STRING_LIST_RIGHT("GUI.Modules.FileManager.YMLEditor.StringList.UpdateLine","&7Update selected line value"),

    FILE_MANAGER_YML_EDITOR_UNKNOWN_NAME("GUI.Modules.FileManager.YMLEditor.UnknownField.Name","&7Unknown Field"),
    FILE_MANAGER_YML_EDITOR_UNKNOWN_LORE("GUI.Modules.FileManager.YMLEditor.UnknownField.Lore","&7To edit this you will need to manually open the file."),

    /* World Manager */
    WORLD_MANAGER_DISPLAY("GUI.Modules.WorldManager.Display","World Manager"),
    WORLD_MANAGER_NAME("GUI.Modules.WorldManager.Name","&aWorld Manager"),
    WORLD_MANAGER_LORE("GUI.Modules.WorldManager.Lore","&7Click to manage all your worlds"),

    WORLD_MANAGER_BROWSER_GUI_TITLE("GUI.Modules.WorldManager.Browser.Title","&aWorld Manage &7> &9Worlds ({WorldAmount})"),
    WORLD_MANAGER_BROWSER_GUI_ITEM_NAME("GUI.Modules.WorldManager.Browser.World.Name","&a{WorldName}"),
    WORLD_MANAGER_BROWSER_GUI_ITEM_LORE_MANAGE("GUI.Modules.WorldManager.Browser.World.Manage","&7Manage World"),
    WORLD_MANAGER_BROWSER_GUI_ITEM_LORE_BACKUP_NOW("GUI.Modules.WorldManager.Browser.World.BackupNow","&7Backup Now"),
    WORLD_MANAGER_BROWSER_GUI_ITEM_LORE_LAST_BACKUP("GUI.Modules.WorldManager.Browser.World.LastBackup","&7Last Backup: &9{LastBackup}"),

    WORLD_MANAGER_WORLD_VIEW_GUI_TITLE("GUI.Modules.WorldManager.WorldView.Title","&aWorlds &7> &9{WorldName}"),
    WORLD_MANAGER_WORLD_VIEW_GUI_CREATE_BACKUP_NAME("GUI.Modules.WorldManager.WorldView.CreateBackup.Name","&aCreate Backup"),
    WORLD_MANAGER_WORLD_VIEW_GUI_CREATE_BACKUP_LORE_CREATE("GUI.Modules.WorldManager.WorldView.CreateBackup.Create","&7Click to create a Backup"),
    WORLD_MANAGER_WORLD_VIEW_GUI_CREATE_BACKUP_LORE_LAST_TIME("GUI.Modules.WorldManager.WorldView.CreateBackup.LastBackupTime","&7Last Backup At: &9{LastBackupAt}"),
    WORLD_MANAGER_WORLD_VIEW_GUI_CREATE_BACKUP_LORE_LAST_PATH("GUI.Modules.WorldManager.WorldView.CreateBackup.LastBackupPath","&7Last Backup Path: &9{LastBackupPath}"),

    /* Backup Manager */
    BACKUP_MANAGER_DISPLAY("GUI.Modules.BackupManager.Display","Backup Manager"),
    BACKUP_MANAGER_NAME("GUI.Modules.BackupManager.Name","&aBackup Manager"),
    BACKUP_MANAGER_LORE("GUI.Modules.BackupManager.Lore","&7Click to manage backups"),

    BACKUP_MANAGER_BROWSER_TITLE("GUI.Modules.BackupManager.BackupBrowser.Title", "&aBackup Browser"),
    BACKUP_MANAGER_BROWSER_ITEM_NAME("GUI.Modules.BackupManager.BackupBrowser.Item.Name", "&a{BackupName}"),
    BACKUP_MANAGER_BROWSER_ITEM_BACKUP_NOW("GUI.Modules.BackupManager.BackupBrowser.Item.Backup Now", "Backup Now"),
    BACKUP_MANAGER_BROWSER_ITEM_RENAME("GUI.Modules.BackupManager.BackupBrowser.Item.Rename", "Rename Backup"),
    BACKUP_MANAGER_BROWSER_ITEM_DELETE("GUI.Modules.BackupManager.BackupBrowser.Item.Delete Backup", "Delete Backup"),
    BACKUP_MANAGER_BROWSER_ITEM_LAST_BACKUP_AT("GUI.Modules.BackupManager.BackupBrowser.Item.LastBackupAt", "&7Last Backup At: &9{LastBackupAt}"),
    BACKUP_MANAGER_BROWSER_ITEM_NEXT_BACKUP_AT("GUI.Modules.BackupManager.BackupBrowser.Item.NextBackupAt", "&7Next Backup At: &9{NextBackupAt}"),
    BACKUP_MANAGER_BROWSER_ITEM_NEXT_BACKUP_IN("GUI.Modules.BackupManager.BackupBrowser.Item.NextBackupIn", "&7Next Backup In: &9{NextBackupIn}"),
    BACKUP_MANAGER_BROWSER_ITEM_SECONDS_LEFT("GUI.Modules.BackupManager.BackupBrowser.Item.SecondsLeft", "&9{SecondsLeftForNextBackup}&7 seconds left"),

    BACKUP_MANAGER_BROWSER_SETTINGS_NAME("GUI.Modules.BackupManager.BackupBrowser.Settings.Name", "&aSettings"),
    BACKUP_MANAGER_BROWSER_SETTINGS_LORE("GUI.Modules.BackupManager.BackupBrowser.Settings.Lore", "&7Click to manage settings"),

    BACKUP_MANAGER_SETTINGS_TITLE("GUI.Modules.BackupManager.Settings.Title","&aBackups &7> &cSettings"),

    BACKUP_MANAGER_SETTINGS_BACKUPS_FOLDER_NAME("GUI.Modules.BackupManager.BackupBrowser.BackupsFolder.Name", "&aBackups Folder"),
    BACKUP_MANAGER_SETTINGS_BACKUPS_FOLDER_LORE("GUI.Modules.BackupManager.BackupBrowser.BackupsFolder.Lore", "&7Click to modify the backups folder."),
    BACKUP_MANAGER_SETTINGS_BACKUPS_FOLDER_PATH("GUI.Modules.BackupManager.BackupBrowser.BackupsFolder.Path", "&7Current Path: &9{BackupsFolderPath}"),

    BACKUP_MANAGER_SETTINGS_SCHEDULE_NAME("GUI.Modules.BackupManager.Settings.Schedule.Name","&aNew Scheduled Backup"),
    BACKUP_MANAGER_SETTINGS_SCHEDULE_LORE("GUI.Modules.BackupManager.Settings.Schedule.Lore","&7Click to schedule a new backup"),

    BACKUP_MANAGER_SETTINGS_SELECT_BACKUPS_FOLDER_NAME("GUI.Modules.BackupManager.Settings.SelectBackupsFolder.Name","&aSelect Backups Folder"),
    BACKUP_MANAGER_SETTINGS_SELECT_BACKUPS_FOLDER_LORE("GUI.Modules.BackupManager.Settings.SelectBackupsFolder.Lore","&7Click to select a new backups folder"),

    BACKUP_MANAGER_SCHEDULE_TITLE("GUI.Modules.BackupManager.Schedule.Title", "&aBackups &7> &cSchedule"),
    BACKUP_MANAGER_SCHEDULE_SELECT_FILES_NAME("GUI.Modules.BackupManager.Schedule.SelectFiles.Name", "&aSelect Files/Folders"),
    BACKUP_MANAGER_SCHEDULE_SELECT_FILES_LORE("GUI.Modules.BackupManager.Schedule.SelectFiles.Lore", "&7Click to select the &6Files/Folders&7 to backup."),

    BACKUP_MANAGER_SCHEDULE_SELECT_TIME_NAME("GUI.Modules.BackupManager.Schedule.SelectTime.Name", "&aSelect Name"),
    BACKUP_MANAGER_SCHEDULE_SELECT_TIME_LORE("GUI.Modules.BackupManager.Schedule.SelectTime.Lore", "&7Click to select the backup time."),

    BACKUP_MANAGER_FILE_BROWSER_TITLE("GUI.Modules.BackupManager.FileBrowser.Title","&aBackups &7> &cFile Browser"),
    BACKUP_MANAGER_FILE_BROWSER_ITEM_NAME("GUI.Modules.BackupManager.FileBrowser.Item.Name", "&a{FileName}"),
    BACKUP_MANAGER_FILE_BROWSER_ITEM_OPEN_FOLDER("GUI.Modules.BackupManager.FileBrowser.Item.OpenFolder", "&7Click to open the folder."),
    BACKUP_MANAGER_FILE_BROWSER_ITEM_ADD_TO_LIST("GUI.Modules.BackupManager.FileBrowser.Item.AddToList", "&7Click to add to the list."),
    BACKUP_MANAGER_FILE_BROWSER_ITEM_REMOVE_FROM_LIST("GUI.Modules.BackupManager.FileBrowser.Item.RemoveFromList", "&7Click to remove from the list."),

    BACKUP_MANAGER_FILE_BROWSER_SAVE_NAME("GUI.Modules.BackupManager.FileBrowser.Save.Name", "&aSave"),
    BACKUP_MANAGER_FILE_BROWSER_SAVE_LORE("GUI.Modules.BackupManager.FileBrowser.Save.Lore", "&7Click to save the selected files (&c{SelectedFilesAmount}&7)"),

    BACKUP_MANAGER_BACKUP_FOLDER_SELECTOR_TITLE("GUI.Modules.BackupManager.BackupFolderSelector.Title", "&aBackups &7> &cBackup Folder Selector"),
    BACKUP_MANAGER_BACKUP_FOLDER_SELECTOR_ITEM_NAME("GUI.Modules.BackupManager.BackupFolderSelector.Item.Name", "&a{FileName}"),
    BACKUP_MANAGER_BACKUP_FOLDER_SELECTOR_ITEM_OPEN_FOLDER("GUI.Modules.BackupManager.BackupFolderSelector.Item.OpenFolder", "&7Click to open the folder."),
    BACKUP_MANAGER_BACKUP_FOLDER_SELECTOR_ITEM_SELECT_FOLDER("GUI.Modules.BackupManager.BackupFolderSelector.Item.SelectFolder", "&7Click to select the folder."),


    ;

    private TranslationManager translationManager;
    private final String path, value;

    L(String path, String value){
        this.path = path;
        this.value = value;
    }

    @Override
    public String getLanguage() {
        return "en";
    }

    @Override
    public Translation get() {
        return new Translation(this, this.path, this.value);
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
