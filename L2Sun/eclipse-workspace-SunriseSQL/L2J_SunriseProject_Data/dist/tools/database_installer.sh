#!/bin/bash
trap finish 2

configure() {
echo "#############################################"
echo "# You entered script configuration area     #"
echo "# No change will be performed in your DB    #"
echo "# I will just ask you some questions about  #"
echo "# your hosts and DB.                        #"
echo "#############################################"
MYSQLDUMPPATH=/usr/bin/mysqldump
MYSQLPATH=/usr/bin/mysql
if [ $? -ne 0 ]; then
echo "We were unable to find MySQL binaries on your path"
while :
 do
  echo -ne "\nPlease enter MySQL binaries directory (no trailing slash): "
  read MYSQLBINPATH
    if [ -e "$MYSQLBINPATH" ] && [ -d "$MYSQLBINPATH" ] && \
       [ -e "$MYSQLBINPATH/mysqldump" ] && [ -e "$MYSQLBINPATH/mysql" ]; then
       MYSQLDUMPPATH="$MYSQLBINPATH/mysqldump"
       MYSQLPATH="$MYSQLBINPATH/mysql"
       break
    else
       echo "The data you entered is invalid. Please verify and try again."
       exit 1
    fi
 done
fi
#LS
echo -ne "\nPlease enter MySQL Login Server hostname (default localhost): "
read LSDBHOST
if [ -z "$LSDBHOST" ]; then
  LSDBHOST="localhost"
fi
echo -ne "\nPlease enter MySQL Login Server database name (default l2jls): "
read LSDB
if [ -z "$LSDB" ]; then
  LSDB="l2jls"
fi
echo -ne "\nPlease enter MySQL Login Server user (default root): "
read LSUSER
if [ -z "$LSUSER" ]; then
  LSUSER="root"
fi
echo -ne "\nPlease enter MySQL Login Server $LSUSER's password (won't be displayed) :"
stty -echo
read LSPASS
stty echo
echo ""
if [ -z "$LSPASS" ]; then
  echo "Hum.. I'll let it be but don't be stupid and avoid empty passwords"
elif [ "$LSUSER" == "$LSPASS" ]; then
  echo "You're not too brilliant choosing passwords huh?"
fi
#GS
echo -ne "\nPlease enter MySQL Game Server hostname (default $LSDBHOST): "
read GSDBHOST
if [ -z "$GSDBHOST" ]; then
  GSDBHOST="localhost"
fi
echo -ne "\nPlease enter MySQL Game Server database name (default l2jgs): "
read GSDB
if [ -z "$GSDB" ]; then
  GSDB="l2jgs"
fi
echo -ne "\nPlease enter MySQL Game Server user (default $LSUSER): "
read GSUSER
if [ -z "$GSUSER" ]; then
  GSUSER="root"
fi
echo -ne "\nPlease enter MySQL Game Server $GSUSER's password (won't be displayed): "
stty -echo
read GSPASS
stty echo
echo ""
if [ -z "$GSPASS" ]; then
  echo "Hum.. I'll let it be but don't be stupid and avoid empty passwords"
elif [ "$GSUSER" == "$GSPASS" ]; then
  echo "You're not too brilliant choosing passwords huh?"
fi
save_config $1
}

save_config() {
if [ -n "$1" ]; then
CONF="$1"
else 
CONF="database_installer.rc"
fi
echo ""
echo "With these data I can generate a configuration file which can be read"
echo "on future updates. WARNING: this file will contain clear text passwords!"
echo -ne "Shall I generate config file $CONF? (Y/n):"
read SAVE
if [ "$SAVE" == "y" -o "$SAVE" == "Y" -o "$SAVE" == "" ];then 
cat <<EOF>$CONF
#Configuration settings for L2J-Datapack database installer script
MYSQLDUMPPATH=$MYSQLDUMPPATH
MYSQLPATH=$MYSQLPATH
LSDBHOST=$LSDBHOST
LSDB=$LSDB
LSUSER=$LSUSER
LSPASS=$LSPASS
GSDBHOST=$GSDBHOST
GSDB=$GSDB
GSUSER=$GSUSER
GSPASS=$GSPASS
EOF
chmod 600 $CONF
echo "Configuration saved as $CONF"
echo "Permissions changed to 600 (rw- --- ---)"
elif [ "$SAVE" != "n" -a "$SAVE" != "N" ]; then
  save_config
fi
}

load_config() {
if [ -n "$1" ]; then
CONF="$1"
else 
CONF="database_installer.rc"
fi
if [ -e "$CONF" ] && [ -f "$CONF" ]; then
. $CONF
else
echo "Settings file not found: $CONF"
echo "You can specify an alternate settings filename:"
echo $0 config_filename
echo ""
echo "If file doesn't exist it can be created"
echo "If nothing is specified script will try to work with ./database_installer.rc"
echo ""
configure $CONF
fi
}

ls_backup(){
while :
  do
   clear
   echo ""
   echo -ne "Do you want to make a backup copy of your LSDB? (y/n): "
   read LSB
   if [ "$LSB" == "Y" -o "$LSB" == "y" ]; then
     echo "Trying to make a backup of your Login Server DataBase."
     $MYSQLDUMPPATH --add-drop-table -h $LSDBHOST -u $LSUSER --password=$LSPASS $LSDB > ls_backup.sql
     if [ $? -ne 0 ];then
        clear
		echo ""
        echo "There was a problem accesing your LS database, either it wasnt created or authentication data is incorrect."
        exit 1
     fi
     break
   elif [ "$LSB" == "n" -o "$LSB" == "N" ]; then 
     break
   fi
  done 
ls_ask
}

ls_ask(){
clear
echo ""
echo "LOGINSERVER DATABASE install type:"
echo ""
echo "(f) Full: WARNING! I'll destroy ALL of your existing login"
echo "    data."
echo ""
echo "(u) Upgrade: I'll do my best to preserve all login data."
echo ""
echo "(s) Skip: I'll take you to the communityserver database"
echo "    installation and upgrade options."
echo ""
echo "(q) Quit"
echo ""
echo -ne "LOGINSERVER DB install type: "
read LSASK
case "$LSASK" in
	"f"|"F") ls_cleanup I;;
	"u"|"U") ls_upgrade U;;
	"s"|"S") cs_backup;;
	"q"|"Q") finish;;
	*) ls_ask;;
esac
}

ls_cleanup(){
clear
echo "Deleting Login Server tables for new content."
$MYL < ls_cleanup.sql
ls_install
}

ls_upgrade(){
clear
echo ""
echo "Upgrading structure of Login Server tables."
echo ""
for file in $(ls sql/login/updates/*.sql);do
	$MYL --force < $file 2>> ls_error.log
done
ls_install
}

ls_install(){
clear
if [ "$1" == "I" ]; then 
echo ""
echo "Installing new Login Server content."
echo ""
else
echo ""
echo "Upgrading Login Server content."
echo ""
fi
for login in $(ls sql/login/*.sql);do
	echo "Installing loginserver table : $login"
	$MYL < $login
done
gs_ask
}

gs_backup(){
while :
  do
   clear
   echo ""
   echo -ne "Do you want to make a backup copy of your GSDB? (y/n): "
   read GSB
   if [ "$GSB" == "Y" -o "$GSB" == "y" ]; then
     echo "Trying to create a Game Server DataBase."
     $MYSQLDUMPPATH --add-drop-table -h $GSDBHOST -u $GSUSER --password=$GSPASS $GSDB > gs_backup.sql
     if [ $? -ne 0 ];then
	 clear
     echo ""
     echo "There was a problem accesing your GS database, either it wasnt created or authentication data is incorrect."
     exit 1
     fi
     break
   elif [ "$GSB" == "n" -o "$GSB" == "N" ]; then 
     break
   fi
  done 
gs_ask
}

gs_ask(){
clear
echo ""
echo "GAME SERVER DATABASE install:"
echo ""
echo "(f) Full: WARNING! I'll destroy ALL of your existing character"
echo "    data (i really mean it: items, pets.. ALL)"
echo ""
echo "(u) Upgrade: I'll do my best to preserve all of your character"
echo "    data."
echo ""
echo "(s) Skip: We'll get into the last set of questions (cummulative"
echo "    updates, custom stuff...)"
echo ""
echo "(q) Quit"
echo ""
echo -ne "GAMESERVER DB install type: "
read GSASK
case "$GSASK" in
	"f"|"F") gs_cleanup I;;
	"u"|"U") gs_upgrade U;;
	"s"|"S") custom_ask;;
	"q"|"Q") finish;;
	*) gs_ask;;
esac
}

gs_cleanup(){
clear
echo "Deleting all Game Server tables for new content."
$MYG < gs_cleanup.sql
gs_install
}

gs_upgrade(){
clear
echo ""
echo "Upgrading structure of Game Server tables (this could take awhile, be patient)"
echo ""
for file in $(ls sql/game/updates/*.sql);do
	$MYG --force < $file 2>> gs_error.log
done
gs_install
}

gs_install(){
clear
if [ "$1" == "I" ]; then 
echo ""
echo "Installing new Game Server content."
echo ""
else
echo ""
echo "Upgrading Game Server content."
echo ""
fi
for gs in $(ls sql/game/*.sql);do
	echo "Installing GameServer table : $gs"
	$MYG < $gs
done
sunrise_install
}

sunrise_install(){
clear
if [ "$1" == "I" ]; then 
echo ""
echo "Installing new Sunrise Server content."
echo ""
else
echo ""
echo "Upgrading Sunrise Server content."
echo ""
fi
for gs in $(ls sql/sunrise_sql_files/*.sql);do
	echo "Installing GameServer table : $gs"
	$MYG < $gs
done
custom_ask
}

custom_ask(){
clear
echo ""
echo "L2J provides some Custom Server Tables for non-retail modifications"
echo "in order to avoid override the original Server Tables."
echo ""
echo "Remember that in order to get these additions actually working"
echo "you need to edit your configuration files."
echo ""
echo -ne "Install Custom Server Tables: (y) yes or (n) no ?"
read CSASK
case "$CSASK" in
	"y"|"Y") custom_install;;
	"n"|"N") mod_ask;;
	*) custom_ask;;
esac
}

custom_install(){
clear
echo ""
echo "Installing Custom content."
for custom in $(ls sql/game/custom/*.sql);do 
	echo "Installing custom table: $custom"
	$MYG < $custom
done
clear
mod_ask
}

mod_ask(){
clear
echo ""
echo "L2J provides a basic infraestructure for some non-retail features"
echo "(aka L2J mods) to get enabled with a minimum of changes."
echo ""
echo "Some of these mods would require extra tables in order to work"
echo "and those tables could be created now if you wanted to."
echo ""
echo "Remember that in order to get these additions actually working"
echo "you need to edit your configuration files."
echo ""
echo -ne "Install Mod Server Tables: (y) yes or (n) no ?"
read MDASK
case "$MDASK" in
	"y"|"Y") mod_install;;
	"n"|"N") finish;;
	*) mod_ask;;
esac
}

mod_install(){
clear
echo ""
echo "Installing Mods content."
for mod in $(ls sql/game/mods/*.sql);do
	echo "Installing custom mod table : $mod"
	$MYG < $mod
done
clear
finish
}

finish(){
clear
echo ""
echo "          L2jSunrise Database Installer finished succesfully."
echo ""
echo ""
echo ""
echo ""
echo ""
echo "                          L2jSunrise"
echo ""
echo ""
echo "Thanks for using our files."
echo "Visit http://www.l2jsunrise.com for more info about l2jsunrise."
echo ""
echo ""
echo ""
echo ""
echo ""
echo ""
exit 0
}

clear
load_config $1
MYL="$MYSQLPATH -h $LSDBHOST -u $LSUSER --password=$LSPASS -D $LSDB"
MYG="$MYSQLPATH -h $GSDBHOST -u $GSUSER --password=$GSPASS -D $GSDB"
ls_backup