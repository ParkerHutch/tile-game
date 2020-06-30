public class GameCamera
{
   private float xOffset, yOffset;
   private TileGameV0_5Launcher game;
   
   public GameCamera(TileGameV0_5Launcher game, float xOffset, float yOffset)
   {
      this.xOffset = xOffset;
      this.yOffset = yOffset;
      this.game = game;
   }
   
   public void move(float xAmount, float yAmount)
   {
      xOffset += xAmount;
      yOffset += yAmount;
   }
   
   public void centerOn(Player player)
   {
      xOffset = (player.getCenterX() - game.getWidth() / 2 );
      yOffset = (player.getCenterY() - game.getHeight() / 2 );
      lockOffsetsToMap();
   }
   
   public void lockOffsetsToMap() {
	   // makes sure that the offsets don't let the camera display the whitespace outside the map
	   if (yOffset > 0) {
		   yOffset = 0;
	   }
   }
   public float getxOffset()
   {
      return xOffset;
   }
   public float getyOffset()
   {
      return yOffset;
   }
   public void setxOffset(float n)
   {
      xOffset = n;
   }
   public void setyOffset(float n)
   {
      yOffset = n;
   }
}