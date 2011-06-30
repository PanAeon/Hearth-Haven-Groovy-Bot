/*    */ package haven;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import javax.media.opengl.GL;
/*    */ 
/*    */ public class AvaRender extends TexRT
/*    */ {
/*    */   List<Indir<Resource>> layers;
/*    */   List<Resource.Image> images;
/*    */   boolean loading;
/* 37 */   public static final Coord sz = new Coord(212, 249);
/*    */ 
/*    */   public AvaRender(List<Indir<Resource>> layers) {
/* 40 */     super(sz);
/* 41 */     setlay(layers);
/*    */   }
/*    */ 
/*    */   public void setlay(List<Indir<Resource>> layers) {
/* 45 */     Collections.sort(layers);
/* 46 */     this.layers = layers;
/* 47 */     this.loading = true;
/*    */   }
/*    */ 
/*    */   public boolean subrend(GOut g) {
/* 51 */     if (!this.loading) {
/* 52 */       return false;
/*    */     }
/* 54 */     List<Resource.Image> images = new ArrayList<Resource.Image>();
/* 55 */     this.loading = false;
/* 56 */     for (Indir r : this.layers) {
/* 57 */       if (r.get() == null)
/* 58 */         this.loading = true;
/*    */       else
/* 60 */         images.addAll(((Resource)r.get()).layers(Resource.imgc));
/*    */     }
/* 62 */     Collections.sort(images);
/* 63 */     if (images.equals(this.images))
/* 64 */       return false;
/* 65 */     this.images = images;
/*    */ 
/* 67 */     g.gl.glClearColor(255.0F, 255.0F, 255.0F, 0.0F);
/* 68 */     g.gl.glClear(16384);
/* 69 */     for (Resource.Image i : images)
/* 70 */       g.image(i.tex(), i.o);
/* 71 */     return true;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.AvaRender
 * JD-Core Version:    0.6.0
 */