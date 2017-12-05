if gameinfo.getromname() == "Donkey Kong" then
	Savestate = "Donkey Kong.QuickNes.QuickSave1.State"
	ButtonNames = {
		"A",
		"B",
		"Up",
		"Down",
		"Left",
		"Right",
	}
end

Outputs = #ButtonNames

function getPosition()
	local position = {}
	memory.usememorydomain("System Bus")
	for x = 0,3 do
		mcy = memory.readbyte(0x0200+(x*4))
		mcx = memory.readbyte(0x0203+(x*4))
		position[#position+1] = {["x"]=mcx, ["y"]=mcy}
	end
	return position
end

function getObjects()	--edit for moving platforms
	local objects = {}
	local mvgplat = {}
	local enemies = {}
	memory.usememorydomain("System Bus")
	for add = 1, 11 do
		local status = memory.readbyte(0x0200+(add*10))
		local sprite = memory.readbyte(0x0201+(add*10))
		if status < 255 then
			if sprite == 160 --platform
				then for x = 0, 3 do
					mply = memory.readbyte(0x0200+(add*10)+(x*4))
					mplx = memory.readbyte(0x0203+(add*10)+(x*4))
					mvgplat[#mvgplat+1] = {["x"]=mplx, ["y"]=mply}
				end
			else
				for x = 0, 3 do
					enemy = memory.readbyte(0x0200+(add*10)+(x*4))
					enemx = memory.readbyte(0x0203+(add*10)+(x*4))
					enemies[#enemies+1] = {["x"]=enemx, ["y"]=enemy}
				end
			end
		end
	end
	objects["enemies"] = enemies
	objects["mvgplat"] = mvgplat
	return objects
end

function hexadec(num) --basic hex to dec for states, anything <= 0x99
	x = num % 16
	hex = 10*(num - (x))/16 + x
	return hex
end

function getState()
	local states = {}
	memory.usememorydomain("System Bus")
	states["life"] = hexadec(memory.readbyte(0x0055))
	states["timer"] = hexadec(memory.readbyte(0x002E))*100 + hexadec(memory.readbyte(0x002F))
	states["t_point"] = hexadec(memory.readbyte(0x0021))*10000 + hexadec(memory.readbyte(0x0022))*100 + hexadec(memory.readbyte(0x0023))
	states["i_point"] = hexadec(memory.readbyte(0x0025))*10000 + hexadec(memory.readbyte(0x0026))*100 + hexadec(memory.readbyte(0x0027))
	return states
end

function getItems()
	local items = {}
	memory.usememorydomain("System Bus")
	for x = 0,4 do
		itemy = memory.readbyte(0x02D0+(x*4))
		itemx = memory.readbyte(0x02D3+(x*4))
		items[#items+1] = {["x"]=itemx, ["y"]=itemy}
	end
	return items
end

function Load()
	savestate.load(Savestate)
end

function Save()
	savestate.save(Savestate)
end

function checkFlag() --death flag
	memory.usememorydomain("System Bus")
	local flag = memory.readbyte(0x0096)
	if flag == 255 then
		return 1
	else
		return 0
	end
end

function getStage()	--8x8 pix blocks
	local stage = {}
	local platforms = {}
	local ladders = {}
	local items = {}   	-- for items
	local enemies = {} 	-- for stage 2 death conveyor belt + DK sprite in all stages
	local tiles = {}		-- for stage 3 extra tiles
	local x = 0
	local y = 0
	local xbit = 0
	local ybit = 0
	local val = 0
	memory.usememorydomain("CIRAM (nametables)")
	for i = 0, 959 do 	--0000 to 03BF
		val = memory.readbyte(0x0 + i)
		xbit = x*8
		ybit = y*8
		if val == 63 or (val >= 64 and val <= 66) or val == 74 or val == 75 then
		-- 0x3F, 40 to 42, 4A, 4B
			ladx = xbit
			lady = ybit
			ladders[#ladders+1] =  {["x"]=ladx, ["y"]=lady}
		elseif (val >= 48 and val <= 52) or (val >= 60 and val <= 62) or (val >= 67 and val <= 73) or (val == 98) then
		--0x30 to 34 && 3C to 3E && 43 to 49, 62
			platx = xbit
			platy = ybit
			platforms[#platforms+1] =  {["x"]=platx, ["y"]=platy}
		elseif (val >= 110) and (val <= 115) then --0x6E to 73 items(umbr[4 tiles]+purse[2 tiles])
			itex = xbit
			itey = ybit
			items[#items+1] =  {["x"]=itex, ["y"]=itey}
		elseif val == 99 then --0x63 stg 3 tiles[1 tile]
			tilx = xbit
			tily = ybit
			tiles[#tiles+1] =  {["x"]=tilx, ["y"]=tily}
		elseif (val >= 104 and val <= 207) or (val >= 92 and val <= 97) then --enemies (0x68 to CF dk sprite + 5C to 61 stg 2 conveyor)
			enemx = xbit
			enemy = ybit
			enemies[#enemies+1] = {["x"]=enemx, ["y"]=enemy}
		end

		x = x + 1
		if x > 31 then
			x = 0
			y = y + 1
		end
	end
	stage["platforms"] = platforms
	stage["ladders"] = ladders
	stage["items2"] = items
	stage["tiles"] = tiles
	stage["enemies2"] = enemies
	return stage
end

function clearJoypad()
        controller = {}
        for b = 1, Outputs do
                controller["P1 " .. ButtonNames[b]] = false
        end
        joypad.set(controller)
end

function setOutput() --output from AI
	controller = {}
	for b = 1, Outputs do
	--val == output from java prog
		if val == true then
           	   	controller["P1 " .. ButtonNames[b]] = true
		else
			controller["P1 " .. ButtonNames[b]] = false
		end
    joypad.set(controller)
	end
end


function table_print (tt, indent, done)
  done = done or {}
  indent = indent or 0
  if type(tt) == "table" then
    for key, value in pairs (tt) do
      io.write(string.rep (" ", indent)) -- indent it
      if type (value) == "table" and not done [value] then
        done [value] = true
        io.write(string.format("[%s] => table\n", tostring (key)));
        io.write(string.rep (" ", indent+4)) -- indent it
        io.write("(\n");
        table_print (value, indent + 7, done)
        io.write(string.rep (" ", indent+4)) -- indent it
        io.write(")\n");
      else
        io.write(string.format("[%s] => %s\n",
            tostring (key), tostring(value)))
      end
    end
  else
    io.write(tt .. "\n")
  end
end

function table_print (tt, indent, done)
  done = done or {}
  indent = indent or 0
  if type(tt) == "table" then
    local sb = {}
    for key, value in pairs (tt) do
      table.insert(sb, string.rep (" ", indent)) -- indent it
      if type (value) == "table" and not done [value] then
        done [value] = true
        table.insert(sb, "{\n");
        table.insert(sb, table_print (value, indent + 2, done))
        table.insert(sb, string.rep (" ", indent)) -- indent it
        table.insert(sb, "}\n");
      elseif "number" == type(key) then
        table.insert(sb, string.format("\"%s\"\n", tostring(value)))
      else
        table.insert(sb, string.format(
            "%s = \"%s\"\n", tostring (key), tostring(value)))
       end
    end
    return table.concat(sb)
  else
    return tt .. "\n"
  end
end

function to_string( tbl )
    if  "nil"       == type( tbl ) then
        return tostring(nil)
    elseif  "table" == type( tbl ) then
        return table_print(tbl)
    elseif  "string" == type( tbl ) then
        return tbl
    else
        return tostring(tbl)
    end
end


--main program body
--Load()
-- once per stage
stage = getStage()
platforms = stage["platforms"]
ladders = stage["ladders"]
items2 = stage["items2"]
tiles = stage["tiles"]
enemies2 = stage["enemies2"]

-- per frame
count = 0

controller = {}
controller["P1 Up"] = false
controller["P1 Down"] = false
controller["P1 Left"] = false
controller["P1 Right"] = false
controller["P1 A"] = false
controller["P1 B"] = false
str = ""

function readAll()
	javaFile = io.open("Java.txt", "r")
	io.input(javaFile)
	str = io.read("*a")
	io.close(javaFile)
end

while true do
	count = 0;
	
	
	pcall(readAll) --if we can't read file ignore error
	
	
	
	if str:sub(10,10) == "1" then
		controller["P1 Up"] = true
	else
		controller["P1 Up"] = false
	end
	
	if str:sub(17,17) == "1" then
		controller["P1 Down"] = true
	else
		controller["P1 Down"] = false
	end

	if str:sub(24,24) == "1" then
		controller["P1 Left"] = true
	else
		controller["P1 Left"] = false
	end
	
	if str:sub(32,32) == "1" then
		controller["P1 Right"] = true
	else
		controller["P1 Right"] = false
	end
	
	if str:sub(36,36) == "1" then
		controller["P1 A"] = true
	else
		controller["P1 A"] = false
	end
	
	if str:sub(40,40) == "1" then
		controller["P1 B"] = true
	else
		controller["P1 B"] = false
	end
	if str:sub(48,48) == "1" then
		savestate.load("Donkey Kong.QuickNes.QuickSave1.State")
	end
		
	deathFlag = checkFlag() -- flag == 1 if dead
	inputs = getObjects()
	enemies = inputs["enemies"]
	mvgplat = inputs["mvgplat"]
	items = getItems()
	states = getState()
	position = getPosition()
	str = "Lua=0\nplatforms:\n" .. to_string(platforms) .."\nladders:\n" .. to_string(ladders) .. "\nitems2:\n" .. to_string(items2) .. "\ntiles:\n" .. to_string(tiles) .. "\nenemies2:\n" .. to_string(enemies2) .. "\ndeathFlag:\n" .. deathFlag  .. "\nenemies:\n" .. to_string(enemies) .. "\nmvgplat:\n" .. to_string(mvgplat) .. "\nitems:\n" .. to_string(items) .. "\nstates:\n" .. to_string(states) .. "\nposition:\n" .. to_string(position)
	luaFile = io.open("LUA.txt", "w")
	io.output(luaFile)
	io.write(str)
	io.flush()
	io.close()
	
	count = count + 1;
	
	joypad.set(controller)
	emu.frameadvance()
end
