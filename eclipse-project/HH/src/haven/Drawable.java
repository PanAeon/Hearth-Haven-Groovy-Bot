/*    */ package haven;
/*    */ 
/*    */ public abstract class Drawable extends GAttrib
/*    */ {
/*    */   public Drawable(Gob gob)
/*    */   {
/* 31 */     super(gob);
/*    */   }
/*    */ 
/*    */   public abstract boolean checkhit(Coord paramCoord);
/*    */ 
/*    */   public abstract void setup(Sprite.Drawer paramDrawer, Coord paramCoord1, Coord paramCoord2);
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Drawable
 * JD-Core Version:    0.6.0
 */