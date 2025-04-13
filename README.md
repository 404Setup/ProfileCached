<img src="./ProfileCached.jpg" alt="Logo" align="right" width="150">

ProfileCached
====

Cache online player profiles to reduce requests to the authentication server.

## Note

1. This mod is only available on servers.
2. The server must have "Online Mode" enabled.
3. This mod is NOT intended for servers using "offline mode" and unofficial authentication endpoints (**mods** or **agents** that allow the server to use or support multiple different Yggdrasil APIs at the same time)
4. This mod does not work when you use a Proxy in front of your server (connecting multiple servers, like BungeeCord and Velocity).

## What is the meaning of this mod?
When your server is temporarily unable to connect to Mojang's verification server, 
it is still able to provide available connections to players already in the cache.

In addition, in servers with more daily active users, 
it can also avoid verification server temporarily rejecting your IP by reducing the number of requests

If you reload the configuration file using the command, the cache will be completely empty. 
You need to think about it before doing this.

## Command
- `/pfc reload`
- `/pfc clean <playername>`
- `/pfc cleanall`
- `/pfc size`

**All commands require administrator permission.**

## Download 

- [Modrinth](https://modrinth.com/mod/profile-cached)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/profile-cached)

## License
The ProfileCached mod is released under the terms of the GNU General Public License version 3 (GPL-3). This means that you are free to use, modify, and redistribute the mod subject to the conditions set forth by this license.

**No Warranty**  
This software is provided "as is", without any warranty of any kind, either expressed or implied, including but not limited to the implied warranties of merchantability and fitness for a particular purpose. In no event shall the authors or copyright holders be liable for any claim, damages, or other liability arising from its use.

For the full details of your rights and obligations under the GPL-3 license, please refer to the complete license text.
