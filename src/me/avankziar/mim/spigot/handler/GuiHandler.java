package me.avankziar.mim.spigot.handler;

public class GuiHandler
{
	/*private static BM plugin = BM.getPlugin();
	public static String PDT_PAGE = "page",
			PDT_PLOT_ID = "plot_id";
	
	//Replacer for Displayname & Lore
	public static String 
			ID = "%id%",
			PLOT_NAME = "%plotname%",
			LENGHT = "%l%",
			WIDTH = "%w%",
			AREA = "%area%",
			BUY_COST = "%buycost%",
			TAX_COST = "%tax%",
			OWNER = "%owner%";
	
	public static void openGs(Player player, SettingsLevel settingsLevel, Inventory inv, boolean closeInv, int pagination)
	{
		GuiType gt = GuiType.GS;
		GUIApi gui = new GUIApi(plugin.pluginName, gt.toString(), null, 6, ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Title", "<gold>Immobilen")),
				settingsLevel == null ? SettingsLevel.NOLEVEL : settingsLevel);
		openGui(player, gt, gui, closeInv, pagination);		
	}
	
	private static void openGui(Player player, GuiType gt, GUIApi gui, boolean closeInv, int pagination)
	{
		boolean fillNotDefineGuiSlots = true;
		Material filler = Material.BLACK_STAINED_GLASS_PANE;
		YamlDocument y = plugin.getYamlHandler().getGui(gt.toString());
		switch(gt)
		{
		case GS:
			ArrayList<Plot> plots = Plot.convert(plugin.getMysqlHandler().getList(MysqlType.PLOT, "`id` ASC", pagination*45, 45, "`id` > ?", 0));
			for(int i = 0; i < 45; i++)
			{
				if(plots.size() == 0)
				{
					PlotHandler.log("plots.size() == 0 || plots.size() < i :"+plots.size()+" "+i);
					break;
				}
				if(plots.size() <= i)
				{
					break;
				}
				Plot plot = plots.get(i);
				if(plot == null)
				{
					PlotHandler.log("plot == null :"+plots.size()+" "+i);
					filler(gui, i, filler, fillNotDefineGuiSlots);
					continue;
				}
				String path = null;
				switch(plot.getBuyStatus())
				{
				case BUYABLE:
					path = "green";
					break;
				case BLOCKED_TAX_NOT_PAID:
					path = "orange";
					break;
				case BOUGHT:
					path = "red";
					break;
				}
				ItemStack is = generateItem(y, path, 0, player, plot);
				LinkedHashMap<String, Entry<GUIApi.Type, Object>> map = new LinkedHashMap<>();
				map.put(PDT_PAGE, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
						pagination));
				map.put(PDT_PLOT_ID, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
						plot.getID()));
				gui.add(i, is, SettingsLevel.BASE, true, true, map, getClickFunction(y, path));
			}
		}
		int allgs = plugin.getMysqlHandler().getCount(MysqlType.PLOT, "`id` > ?", 0);
		double pagecount = (double) allgs / 45.0;
		boolean lastpage = pagecount <= Double.valueOf(pagination+1);
		PlotHandler.log("allgs :"+allgs+" | pagecount : "+pagecount+" | lastpage : "+lastpage);
		for(int i = 0; i < 54; i++)
		{
			if(y.get(i+".Material") == null)
			{
				PlotHandler.log("y.get(i+.Material) == null :"+gui.isSlotOccupied(i)+" "+i);
				filler(gui, i, filler, fillNotDefineGuiSlots);
				continue;
			}
			ItemStack is = generateItem(y, String.valueOf(i), 0, player, null);
			switch(gt)
			{
			case GS:
				LinkedHashMap<String, Entry<GUIApi.Type, Object>> map = new LinkedHashMap<>();
				ClickFunction[] cf = getClickFunction(y, String.valueOf(i));
				boolean breaks = false;
				for(ClickFunction c : cf)
				{
					if(c.getFunction().equals(ClickFunctionType.GS_PAGE_NEXT.toString()))
					{
						if(lastpage)
						{
							if(pagecount <= 1)
							{
								breaks = true;
								break;
							}
							map.put(PDT_PAGE, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
									0));
						} else
						{
							map.put(PDT_PAGE, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
									pagination+1));
						}
					} else if(c.getFunction().equals(ClickFunctionType.GS_PAGE_PAST.toString()))
					{
						if(lastpage)
						{
							if(pagecount <= 1)
							{
								breaks = true;
								break;
							}
							map.put(PDT_PAGE, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
									pagination-1));
						} else
						{
							map.put(PDT_PAGE, new AbstractMap.SimpleEntry<GUIApi.Type, Object>(GUIApi.Type.INTEGER,
									(int) Math.floor(pagecount)));
						}
					}
				}
				if(breaks)
				{
					filler(gui, i, filler, fillNotDefineGuiSlots);
					break;
				}
				gui.add(i, is, SettingsLevel.BASE, true, true, map, cf);
			}
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(closeInv)
				{
					player.closeInventory();
				}
				gui.open(player, gt);
			}
		}.runTask(plugin);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack generateItem(YamlDocument y, String parentPath, int overrideAmount, Player player, Plot plot)
	{
		if(y.get(parentPath+".Material") == null)
		{
			return null;
		}
		int amount = 1;
		if(y.get(parentPath+".Amount") != null)
		{
			amount = y.getInt(parentPath+".Amount");
		}
		if(overrideAmount > 0)
		{
			amount = overrideAmount;
		}
		Material mat = Material.valueOf(y.getString(parentPath+".Material"));
		ItemStack is = null;
		if(mat == Material.PLAYER_HEAD && y.get(parentPath+".HeadTexture") != null)
		{
			is = getSkull(y.getString(parentPath+".HeadTexture"), amount);
		} else
		{
			is = new ItemStack(mat, amount);
		}
		ItemMeta im = is.getItemMeta();
		if(y.get(parentPath+".Displayname") != null)
		{
			im.displayName(ChatApi.tl(getStringPlaceHolder(player, plot, y.getString(parentPath+".Displayname"), player.getName())));
		}
		if(y.get(parentPath+".CustomModelData") != null)
		{
			im.setCustomModelData(y.getInt(parentPath+".CustomModelData"));
		}
		if(y.get(parentPath+".ItemFlag") != null)
		{
			for(String s : y.getStringList(parentPath+".ItemFlag"))
			{
				try
				{
					im.addItemFlags(ItemFlag.valueOf(s));
				} catch(Exception e)
				{
					continue;
				}
			}
		}
		if(mat == Material.ENCHANTED_BOOK)
		{
			if(im instanceof EnchantmentStorageMeta)
			{
				EnchantmentStorageMeta esm = (EnchantmentStorageMeta) im;
				for(String s : y.getStringList(parentPath+".Enchantment"))
				{
					String[] split = s.split(":");
					if(split.length != 2)
					{
						if(!s.isEmpty() || !s.isBlank())
						{
							MDGS.logger.info("Enchantment Failed! '"+s+"' Lenght != 2 ");
						}
						continue;
					}					
					try
					{
						NamespacedKey nsk = NamespacedKey.minecraft(split[0].toLowerCase());
						Enchantment e = Registry.ENCHANTMENT.get(nsk);
						esm.addStoredEnchant(e, Integer.parseInt(split[1]), true);
					} catch(Exception e)
					{
						MDGS.logger.info("Enchantment Failed! '"+s+"' | "+e.getCause().getClass().getName());
						continue;
					}
				}
				is.setItemMeta(esm);
				im = is.getItemMeta();
			}
		} else
		{
			if(y.get(parentPath+".Enchantment") != null)
			{
				for(String s : y.getStringList(parentPath+".Enchantment"))
				{
					String[] split = s.split(":");
					if(split.length != 2)
					{
						if(!s.isEmpty() || !s.isBlank())
						{
							MDGS.logger.info("Enchantment Failed! '"+s+"' Lenght != 2 ");
						}
						continue;
					}					
					try
					{
						NamespacedKey nsk = NamespacedKey.minecraft(split[0].toLowerCase());
						Enchantment e = Registry.ENCHANTMENT.get(nsk);
						im.addEnchant(e, Integer.parseInt(split[1]), true);
					} catch(Exception e)
					{
						MDGS.logger.info("Enchantment Failed! "+s+" | "+e.getCause().getClass().getName());
						continue;
					}
				}
			}
		}
		if(y.get(parentPath+".Lore") != null)
		{
			ArrayList<String> lo = new ArrayList<>();
			ArrayList<Component> lore = new ArrayList<>();
			for(String s : y.getStringList(parentPath+".Lore"))
			{
				lo.add(s);
			}
			lore = (ArrayList<Component>) getLorePlaceHolder(player, plot, lo, player.getName());
			im.lore(lore);
		}
		is.setItemMeta(im);
		if(y.get(parentPath+".ArmorMeta.TrimMaterial") != null 
				&& y.get(parentPath+".ArmorMeta.TrimPattern") != null 
				&& im instanceof ArmorMeta)
		{
			ArmorMeta ima = (ArmorMeta) im;
			try
			{
				ima.setTrim(new ArmorTrim(getTrimMaterial(y.getString(parentPath+".ArmorMeta.TrimMaterial")),
						getTrimPattern(y.getString(parentPath+".ArmorMeta.TrimPattern"))));
			} catch(Exception e)
			{
				ima.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.WILD));
			}
			is.setItemMeta(ima);
			im = is.getItemMeta();
		}
		if(y.get(parentPath+".AxolotlBucket") != null && im instanceof AxolotlBucketMeta)
		{
			AxolotlBucketMeta imm = (AxolotlBucketMeta) im;
			try
			{
				imm.setVariant(Axolotl.Variant.valueOf(y.getString(parentPath+".AxolotlBucket")));
			} catch(Exception e)
			{
				imm.setVariant(Axolotl.Variant.BLUE);
			}
			is.setItemMeta(imm);
			im = is.getItemMeta();
		}
		if(y.get(parentPath+".Banner") != null && im instanceof BannerMeta)
		{
			BannerMeta imm = (BannerMeta) im;
			for(String s : y.getStringList(parentPath+".Banner"))
			{
				String[] split = s.split(";");
				if(split.length != 2)
				{
					continue;
				}
				try
				{
					imm.addPattern(new Pattern(DyeColor.valueOf(split[0]), PatternType.valueOf(split[1])));
				} catch(Exception e)
				{
					continue;
				}
			}
			is.setItemMeta(imm);
			im = is.getItemMeta();
		}
		if(im instanceof BookMeta)
		{
			BookMeta imm = (BookMeta) im;
			try
			{
				if(y.get(parentPath+".Book.Author") != null)
				{
					imm.setAuthor(y.getString(parentPath+".Book.Author"));
				}
				if(y.get(parentPath+".Book.Generation") != null)
				{
					imm.setGeneration(Generation.valueOf(y.getString(parentPath+".Book.Generation")));
				}
				if(y.get(parentPath+".Book.Title") != null)
				{
					imm.setTitle(y.getString(parentPath+".Book.Title"));
				}
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		if(y.get(parentPath+".Durability") != null && im instanceof Damageable)
		{
			Damageable imm = (Damageable) im;
			try
			{
				imm.setDamage(getMaxDamage(mat)-y.getInt(parentPath+".Durability"));
			} catch(Exception e)
			{
				imm.setDamage(0);
			}
			is.setItemMeta(imm);
			im = is.getItemMeta();
		}
		if(y.get(parentPath+".LeatherArmor.Color.Red") != null 
				&& y.get(parentPath+".LeatherArmor.Color.Green") != null 
				&& y.get(parentPath+".LeatherArmor.Color.Blue") != null 
				&& im instanceof LeatherArmorMeta)
		{
			LeatherArmorMeta imm = (LeatherArmorMeta) im;
			try
			{
				imm.setColor(Color.fromRGB(
						y.getInt(parentPath+".LeatherArmor.Color.Red"),
						y.getInt(parentPath+".LeatherArmor.Color.Green"),
						y.getInt(parentPath+".LeatherArmor.Color.Blue")));
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		if(im instanceof PotionMeta)
		{
			PotionMeta imm = (PotionMeta) im;
			try
			{
				if(y.get(parentPath+".Potion.PotionEffectType") != null 
						&& y.get(parentPath+".Potion.Duration") != null 
						&& y.get(parentPath+".Potion.Amplifier") != null)
				{
					imm.addCustomEffect(new PotionEffect(
							PotionEffectType.getByName(y.getString(parentPath+".Potion.PotionEffectType")),
							y.getInt(parentPath+".Potion.Duration"),
							y.getInt(parentPath+".Potion.Amplifier")), true);
				}
				if(y.get(parentPath+".Potion.Color.Red") != null 
						&& y.get(parentPath+".Potion.Color.Green") != null 
						&& y.get(parentPath+".Potion.Color.Blue") != null)
				{
					imm.setColor(Color.fromRGB(
						y.getInt(parentPath+".Potion.Color.Red"),
						y.getInt(parentPath+".Potion.Color.Green"),
						y.getInt(parentPath+".Potion.Color.Blue")));
				}
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		if(y.get(parentPath+".Repairable") != null && im instanceof Repairable)
		{
			Repairable imm = (Repairable) im;
			try
			{
				imm.setRepairCost(y.getInt(parentPath+".Repairable"));
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		if(y.get(parentPath+".TropicalFishBucket.BodyColor") != null 
				&& y.get(parentPath+".TropicalFishBucket.Pattern") != null 
				&& y.get(parentPath+".TropicalFishBucket.PatternColor") != null 
				&& im instanceof TropicalFishBucketMeta)
		{
			TropicalFishBucketMeta imm = (TropicalFishBucketMeta) im;
			try
			{
				imm.setBodyColor(DyeColor.valueOf(y.getString(parentPath+".TropicalFishBucket.BodyColor")));
				imm.setPattern(TropicalFish.Pattern.valueOf(y.getString(parentPath+".TropicalFishBucket.Pattern")));
				imm.setPatternColor(DyeColor.valueOf(y.getString(parentPath+".TropicalFishBucket.PatternColor")));
				is.setItemMeta(imm);
				im = is.getItemMeta();
			} catch(Exception e){}
		}
		return is;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getSkull(String url, int amount) 
	{
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, amount, (short) 3);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "null");
        byte[] encodedData = org.apache.commons.codec.binary.Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }
	
	public static List<Component> getLorePlaceHolder(Player player, Plot plot,
			List<String> lore, String playername)
	{
		List<Component> list = new ArrayList<>();
		for(String s : lore)
		{
			String a = getStringPlaceHolder(player, plot, s, playername);
			if(a == null)
			{
				continue;
			}
			a = getStringPlaceHolderVault(player, plot, a, playername);
			if(a == null)
			{
				continue;
			}
			list.add(ChatApi.tl(a));
		}
		return list;
	}
	
	private static String getStringPlaceHolder(Player player, Plot plot,
			String text, String playername)
	{
		String s = text;
		if(plot != null)
		{
			if(text.contains(ID))
			{
				s = s.replace(ID, String.valueOf(plot.getID()));
			}
			if(text.contains(OWNER))
			{
				s = s.replace(OWNER, plot.getOwnerUUID() != null ? Bukkit.getOfflinePlayer(plot.getOwnerUUID()).getName() : "/");
			}
			if(text.contains(LENGHT))
			{
				s = s.replace(LENGHT, String.valueOf(plot.getLenght()));
			}
			if(text.contains(WIDTH))
			{
				s = s.replace(WIDTH, String.valueOf(plot.getWidth()));
			}
			if(text.contains(AREA))
			{
				s = s.replace(AREA, String.valueOf(plot.getArea()));
			}
			if(text.contains(PLOT_NAME))
			{
				s = s.replace(PLOT_NAME, String.valueOf(plot.getName()));
			}
		}
		return s;
	}
	
	private static String getStringPlaceHolderVault(Player player, Plot plot,
			String text, String playername)
	{
		String s = text;
		if(plot != null)
		{
			if(text.contains(BUY_COST))
			{
				s = s.replace(BUY_COST, String.valueOf(plot.getBuyCost()+plugin.getVaultEco().currencyNamePlural()));
			}
			if(text.contains(TAX_COST))
			{
				s = s.replace(TAX_COST, String.valueOf(plot.getTax(PlotHandler.getCostPerBlock())+plugin.getVaultEco().currencyNamePlural()));
			}
		}
		return s;
	}
	
	private static int getMaxDamage(Material material)
	{
		int damage = 0;
		switch(material)
		{
		case WOODEN_AXE: //Fallthrough
		case WOODEN_HOE:
		case WOODEN_PICKAXE:
		case WOODEN_SHOVEL:
		case WOODEN_SWORD:
			damage = 60;
			break;
		case LEATHER_BOOTS:
			damage = 65;
			break;
		case LEATHER_CHESTPLATE:
			damage = 80;
			break;
		case LEATHER_HELMET:
			damage = 55;
			break;
		case LEATHER_LEGGINGS:
			damage = 75;
			break;
		case STONE_AXE:
		case STONE_HOE:
		case STONE_PICKAXE:
		case STONE_SHOVEL:
		case STONE_SWORD:
			damage = 132;
			break;
		case CHAINMAIL_BOOTS:
			damage = 196;
			break;
		case CHAINMAIL_CHESTPLATE:
			damage = 241;
			break;
		case CHAINMAIL_HELMET:
			damage = 166;
			break;
		case CHAINMAIL_LEGGINGS:
			damage = 226;
			break;
		case GOLDEN_AXE:
		case GOLDEN_HOE:
		case GOLDEN_PICKAXE:
		case GOLDEN_SHOVEL:
		case GOLDEN_SWORD:
			damage = 33;
			break;
		case GOLDEN_BOOTS:
			damage = 91;
			break;
		case GOLDEN_CHESTPLATE:
			damage = 112;
			break;
		case GOLDEN_HELMET:
			damage = 77;
			break;
		case GOLDEN_LEGGINGS:
			damage = 105;
			break;
		case IRON_AXE:
		case IRON_HOE:
		case IRON_PICKAXE:
		case IRON_SHOVEL:
		case IRON_SWORD:
			damage = 251;
			break;
		case IRON_BOOTS:
			damage = 195;
			break;
		case IRON_CHESTPLATE:
			damage = 40;
			break;
		case IRON_HELMET:
			damage = 165;
			break;
		case IRON_LEGGINGS:
			damage = 225;
			break;
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_PICKAXE:
		case DIAMOND_SHOVEL:
		case DIAMOND_SWORD:
			damage = 1562;
			break;
		case DIAMOND_BOOTS:
			damage = 429;
			break;
		case DIAMOND_CHESTPLATE:
			damage = 528;
			break;
		case DIAMOND_HELMET:
			damage = 363;
			break;
		case DIAMOND_LEGGINGS:
			damage = 495;
			break;
		case NETHERITE_AXE:
		case NETHERITE_HOE:
		case NETHERITE_PICKAXE:
		case NETHERITE_SHOVEL:
		case NETHERITE_SWORD:
			damage = 2031;
			break;
		case NETHERITE_BOOTS:
			damage = 482;
			break;
		case NETHERITE_CHESTPLATE:
			damage = 592;
			break;
		case NETHERITE_HELMET:
			damage = 408;
			break;
		case NETHERITE_LEGGINGS:
			damage = 556;
			break;
		case SHIELD:
			damage = 337;
			break;
		case TURTLE_HELMET:
			damage = 276;
			break;
		case TRIDENT:
			damage = 251;
			break;
		case FISHING_ROD:
			damage = 65;
			break;
		case CARROT_ON_A_STICK:
			damage = 26;
			break;
		case WARPED_FUNGUS_ON_A_STICK:
			damage = 100;
			break;
		case ELYTRA:
			damage = 432;
			break;
		case SHEARS:
			damage = 238;
			break;
		case BOW:
			damage = 385;
			break;
		case CROSSBOW:
			damage = 326;
			break;
		case FLINT_AND_STEEL:
			damage = 65;
			break;
		default:
			damage = 0;
			break;
		}
		return damage;
	}
	
	public static TrimMaterial getTrimMaterial(String s)
	{
		switch(s)
		{
		default:
			return TrimMaterial.IRON;
		case "AMETHYST":
			return TrimMaterial.AMETHYST;
		case "COPPER":
			return TrimMaterial.COPPER;
		case "DIAMOND":
			return TrimMaterial.DIAMOND;
		case "EMERALD":
			return TrimMaterial.EMERALD;
		case "GOLD":
			return TrimMaterial.GOLD;
		case "IRON":
			return TrimMaterial.IRON;
		case "LAPIS":
			return TrimMaterial.LAPIS;
		case "NETHERITE":
			return TrimMaterial.NETHERITE;
		case "QUARTZ":
			return TrimMaterial.QUARTZ;
		case "REDSTONE":
			return TrimMaterial.REDSTONE;
		}
	}
	
	public static TrimPattern getTrimPattern(String s)
	{
		switch(s)
		{
		default:
			return TrimPattern.WILD;
		case "COAST":
			return TrimPattern.COAST;
		case "DUNE":
			return TrimPattern.DUNE;
		case "EYE":
			return TrimPattern.EYE;
		case "HOST":
			return TrimPattern.HOST;
		case "RAISER":
			return TrimPattern.RAISER;
		case "RIB":
			return TrimPattern.RIB;
		case "SENTRY":
			return TrimPattern.SENTRY;
		case "SHAPER":
			return TrimPattern.SHAPER;
		case "SILENCE":
			return TrimPattern.SILENCE;
		case "SNOUT":
			return TrimPattern.SNOUT;
		case "SPIRE":
			return TrimPattern.SPIRE;
		case "TIDE":
			return TrimPattern.TIDE;
		case "VEX":
			return TrimPattern.VEX;
		case "WARD":
			return TrimPattern.WARD;
		case "WILD":
			return TrimPattern.WILD;
		}
	}
	
	private static ClickFunction[] getClickFunction(YamlDocument y, String pathBase)
	{
		ArrayList<ClickFunction> ctar = new ArrayList<>();
		List<ClickType> list = new ArrayList<ClickType>(EnumSet.allOf(ClickType.class));
		for(ClickType ct : list)
		{
			if(y.get(pathBase+".ClickFunction."+ct.toString()) == null)
			{
				continue;
			}
			ClickFunctionType cft = null;
			try
			{
				cft = ClickFunctionType.valueOf(y.getString(pathBase+".ClickFunction."+ct.toString()));
			} catch(Exception e)
			{
				continue;
			}
			ctar.add(new ClickFunction(ct, cft));
		}
		return ctar.toArray(new ClickFunction[ctar.size()]);
	}
	
	private static void filler(GUIApi gui, int i, Material mat, boolean fillNotDefineGuiSlots)
	{
		if(fillNotDefineGuiSlots)
		{
			if(gui.isSlotOccupied(i))
			{
				return;
			}
		}
		ItemStack is = new ItemStack(mat, 1);
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.addItemFlags(ItemFlag.HIDE_DESTROYS);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		im.displayName(ChatApi.tl("&0"));
		im.lore(new ArrayList<>());
		is.setItemMeta(im);
		LinkedHashMap<String, Entry<GUIApi.Type, Object>> map = new LinkedHashMap<>();
		gui.add(i, is, SettingsLevel.NOLEVEL, true, false, map, new ClickFunction[0]);
	}*/
}