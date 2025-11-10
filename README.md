# Command Schedular
Schedule commands to run at specific points.

## Setup
- Add Command Schedular jar into your plugins folder
- Restart your server
- Edit commands.yml to your liking
- Type /commandschedular reload

## Commands
- **/commandschedular help:** Show the help message
- **/commandschedular reload:** Reload the plugin
- **/commandschedular execute:** Execute a specific configured command immediately
- **/commandschedular time:** Checks the current server time. Useful for configuring a [cron](#triggers).

## Permissions
- **commandschedular.reload:** Allows the use of the reload command
- **commandschedular.execute:** Allows the use of the execute command
- **commandschedular.time:** Allows the use of the time command

## Scheduled Commands
```yaml
# This example will say hello to all players every 30 seconds.
hello-players:
  commands:
    - 'message: Hello, {player}!'
  triggers:
    interval-ticks: 600

# This example will broadcast random help messages every 10 minutes.
random-help-message:
  commands:
    - 'broadcast: Remember to read the rules!'
    - 'broadcast: Need help? Ask a staff member!'
    - 'broadcast: Check out our website for more info!'
  only-run-one-random-command: true
  triggers:
    interval-ticks: 12000

# This example will give all players $100 every 5 minutes.
five-minute-cash:
  commands:
    - 'console-for-each-player: eco give {player} 100'
  triggers:
    cron: 0 0 */5 * * * * # tick, second, minute, hour, day of month, month, day of week

# This example will broadcast a message one minute after the server starts.
one-minute-message:
  commands:
    - 'broadcast: The server has been running for one minute!'
  triggers:
    ticks-from-server-start: 1200

# This example will make all players run /spawn every hour on the hour.
hourly-spawn:
  commands:
    - 'for-each-player: spawn'
  triggers:
    cron: 0 0 0 * * * * # tick, second, minute, hour, day of month, month, day of week

# This example will stop the server at midnight every day.
midnight-shutdown:
  commands:
    - 'console: stop'
  triggers:
    cron: 0 0 0 0 * * * # tick, second, minute, hour, day of month, month, day of week
```

## Settings
- **commands:** Commands to run when the command configuration is executed. Syntax: &lt;commandType&gt;:&lt;command&gt;. See [Command Types](#command-types).
- **triggers:** Control when the command configuration is executed.
- **only-run-one-random-command:** Only runs one random command instead of all commands when executed.

## Command Types
- **console:** Executes the command as the console.
- **console-for-each-player:** Executes the command as the console for each online player, replacing {player} with the player's name.
- **for-each-player:** Executes the command as each online player, replacing {player} with the player's name.
- **broadcast:** Broadcasts a message to all online players.
- **message:** Sends a message to all online players, replacing {player} with the player's name.

## Triggers
- **interval-ticks:** The number of ticks between each execution of the command configuration.
- **cron:** A cron to control specific points that the command configuration will execute down to the tick. Syntax: &lt;second&gt; &lt;minute&gt; &lt;hour&gt; &lt;dayOfMonth&gt; &lt;month&gt; &lt;dayOfWeek&gt; &lt;year&gt;.
- **ticks-from-server-start:** The number of ticks between each condition check. Can have multiple values comma separated.

## Execute Conditions
- **min-players-online:** Commands will only execute if this many players are online.
- **min-players-who-meet-conditions:** Commands will only execute if this many players meet conditions.
- **max-players-who-meet-conditions:** Commands will only execute if less than this many players meet conditions.
- **all-players-meet-conditions:** Commands will only execute if all players meet conditions.

## Player Conditions
- **worlds:** Commands will only execute if the player is in one of these worlds.
- **environments:** Commands will only execute if the player is in one of these environments.
- **biomes:** Commands will only execute if the player is in one of these biomes.
- **gamemodes:** Commands will only execute if the player is in one of these gamemodes.
- **has-permissions:** Commands will only execute if the player has all of these permissions.
- **missing-permissions:** Commands will not execute if the player has any of these permissions.
- **min-x:** Commands will only execute if the player is above this x value.
- **min-y:** Commands will only execute if the player is above this y value.
- **min-z:** Commands will only execute if the player is above this z value.
- **max-x:** Commands will only execute if the player is below this x value.
- **max-y:** Commands will only execute if the player is below this y value.
- **max-z:** Commands will only execute if the player is below this z value.
- **must-be-in-water:** Commands will only execute if the player is in water.
- **must-not-be-in-water:** Commands will only execute if the player is not in water.