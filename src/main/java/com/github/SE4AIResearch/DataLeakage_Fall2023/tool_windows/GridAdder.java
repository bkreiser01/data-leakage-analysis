package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import java.awt.*;

public class GridAdder {
   public static void addObject(Component component, Container container, GridBagLayout layout, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty){
      gbc.gridx = gridx;
      gbc.gridy = gridy;

      gbc.gridwidth = gridwidth;
      gbc.gridheight = gridheight;

      gbc.weightx = weightx;
      gbc.weighty = weighty;

      layout.setConstraints(component, gbc);
      container.add(component);
   }
}
