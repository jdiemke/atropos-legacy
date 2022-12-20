package atropos.core.model.milkshape;

import java.util.Vector;

import atropos.core.math.Matrix4f;
import atropos.core.math.Quaternion;
import atropos.core.math.Vector3f;

public class MS3DJoint {
	
	public String name;
	public String parentName;
	
	public Vector3f rotation;
	public Vector3f translation;
	
	public MS3DJoint parent;
	public MS3DJoint child;
	
	public Vector<MS3DRotationKeyFrame> rotationKeyFrames = new Vector<MS3DRotationKeyFrame>();
	public Vector<MS3DTranslationKeyFrame> translationKeyFrames = new Vector<MS3DTranslationKeyFrame>();
	
	Matrix4f relative;
	Matrix4f result;
	Matrix4f finalRelative;
	Matrix4f finalResult;
	
	public void computeInitial() {
		Matrix4f trans = Matrix4f.constructTranslationMatrix(translation.x, translation.y, translation.z);
		Matrix4f xrot = Matrix4f.constructXRotationMatrix(rotation.x);
		Matrix4f yrot = Matrix4f.constructYRotationMatrix(rotation.y);
		Matrix4f zrot = Matrix4f.constructZRotationMatrix(rotation.z);
		
		relative = trans.multiply(zrot.multiply(yrot).multiply(xrot));
		finalRelative = relative;
		
		if(parent==null) {
			result= relative;
			finalResult = result;
		}
		else {
			result = parent.result.multiply(relative);
			finalResult = result;
		}
		
	}
	
	long start = System.currentTimeMillis();
	
	public void computeFrame(float animationFPS, float totalFrames) {
		int startFrame= 1;//122;
		int endFrame=400;//150;
		float speed = 0.4f;
		
		float startTime = startFrame / (float)animationFPS;
		float endTime = endFrame / (float)animationFPS;
		
		float elapsed = System.currentTimeMillis()-start;
		float time = elapsed/1000.f;
		time = (elapsed/1000f*speed) % ((endTime -startTime))+ startTime;
		
		MS3DRotationKeyFrame startRot = null, endRot = null;
		
		endRot = rotationKeyFrames.get(rotationKeyFrames.size()-1);
		startRot = rotationKeyFrames.get(rotationKeyFrames.size()-2);
		
		for(int i=1; i < rotationKeyFrames.size(); i++) {
			
			if(time <= rotationKeyFrames.get(i).time) {
				endRot = rotationKeyFrames.get(i);
				startRot = rotationKeyFrames.get(i-1);
				break;
			}
		}
		
		float delta = (time -startRot.time)/(endRot.time - startRot.time);
		if(delta <0) delta = 0;
		if(delta >1) delta = 1;
		
	MS3DTranslationKeyFrame startPos = null, endPos = null;
		
		endPos = translationKeyFrames.get(translationKeyFrames.size()-1);
		startPos = translationKeyFrames.get(translationKeyFrames.size()-2);
		
		for(int i=1; i < translationKeyFrames.size(); i++) {
			
			if(time <= translationKeyFrames.get(i).time) {
				endPos = translationKeyFrames.get(i);
				startPos = translationKeyFrames.get(i-1);
				break;
			}
		}
		
		float delta2 = (time -startPos.time)/(endRot.time - startPos.time);
		if(delta2 <0) delta2 = 0;
		if(delta2 >1) delta2 = 1;
		

		
		MS3DRotationKeyFrame rotKey = startRot;
		MS3DRotationKeyFrame rotKey2 =endRot;
		MS3DTranslationKeyFrame posKey = translationKeyFrames.get(0);
		
		Quaternion quat1 = new Quaternion(rotKey.rotation.x,rotKey.rotation.y,rotKey.rotation.z);
		Quaternion quat2 = new Quaternion(rotKey2.rotation.x,rotKey2.rotation.y,rotKey2.rotation.z);
		
	
		
		Quaternion 
	interpolation = quat1.slerp2(quat2, delta);
		
		
		
		MS3DTranslationKeyFrame transl = startPos;
		MS3DTranslationKeyFrame transl2 = endPos;
		
		Vector3f finalTrans = transl.position.add(transl2.position.substract(transl.position).multiply(delta2));
		
		Vector3f translation = posKey.position;
		Vector3f rotation = rotKey.rotation;
		
		Matrix4f trans = Matrix4f.constructTranslationMatrix(
				finalTrans.x,
				finalTrans.y,
				finalTrans.z);
		
	
		
		Matrix4f xrot = Matrix4f.constructXRotationMatrix(this.rotation.x);
		Matrix4f yrot = Matrix4f.constructYRotationMatrix(this.rotation.y);
		Matrix4f zrot = Matrix4f.constructZRotationMatrix(this.rotation.z);
		
		Matrix4f transformation = trans.multiply(zrot.multiply(yrot).multiply(xrot));
		Matrix4f rot = zrot.multiply(yrot).multiply(xrot);
		 transformation = relative.multiply(trans.multiply(interpolation.normalize().toMatrix()));
		finalRelative = transformation;
		//System.out.println(finalRelative);
		
		if(parent==null) {
			finalResult =finalRelative;
		}
		else {
			finalResult = parent.finalResult.multiply(finalRelative);
			
		}
	}
	
//	public void computeFrame(float animationFPS, float totalFrames) {
//		int startFrame= 1;
//		int endFrame=25;
//		
//		long now = System.currentTimeMillis();
//		
//		int animLength =rotationKeyFrames.size() -1;
//		
//		int animLength2 =translationKeyFrames.size() ;  
//		
//		long elapsed = (now - start);
//		float realFrame = (elapsed *0.2f*  10)/1000.0f;
//		float frac = realFrame % 1.0f;
//		int frame =  (((int)realFrame)%animLength);
//		int frame2 = (((int)realFrame+1)%animLength);
//		
//		int frame3 =  (((int)realFrame)%animLength2);
//		int frame4 = (((int)realFrame+1)%animLength2);
//		
//		
//		MS3DRotationKeyFrame rotKey = rotationKeyFrames.get(frame);
//		MS3DRotationKeyFrame rotKey2 = rotationKeyFrames.get(frame2);
//		MS3DTranslationKeyFrame posKey = translationKeyFrames.get(0);
//		
//		Quaternion quat1 = new Quaternion(rotKey.rotation.x,rotKey.rotation.y,rotKey.rotation.z);
//		Quaternion quat2 = new Quaternion(rotKey2.rotation.x,rotKey2.rotation.y,rotKey2.rotation.z);
//		
//	
//		
//		Quaternion interpolation = quat1.add(quat2.substract(quat1).multiply(frac));
//	interpolation = quat1.slerp2(quat2, frac);
//		
//		
//		float x = rotKey.rotation.x +(rotKey2.rotation.x -rotKey.rotation.x)*frac;
//		float y = rotKey.rotation.y +(rotKey2.rotation.y -rotKey.rotation.y)*frac;
//		float z = rotKey.rotation.z +(rotKey2.rotation.z -rotKey.rotation.z)*frac;
//		
//		MS3DTranslationKeyFrame transl = translationKeyFrames.get(frame3);
//		MS3DTranslationKeyFrame transl2 = translationKeyFrames.get(frame4);
//		
//		Vector3f finalTrans = transl.position.add(transl2.position.substract(transl.position).multiply(frac));
//		
//		Vector3f translation = posKey.position;
//		Vector3f rotation = rotKey.rotation;
//		
//		Matrix4f trans = Matrix4f.constructTranslationMatrix(
//				translation.x+ this.translation.x,
//				translation.y+ this.translation.y,
//				translation.z+ this.translation.z);
//		
//		 trans = Matrix4f.constructTranslationMatrix(
//				translation.x,
//				 translation.y,
//				 translation.z);
//		
//		Matrix4f xrot = Matrix4f.constructXRotationMatrix(this.rotation.x);
//		Matrix4f yrot = Matrix4f.constructYRotationMatrix(this.rotation.y);
//		Matrix4f zrot = Matrix4f.constructZRotationMatrix(this.rotation.z);
//		
//		Matrix4f transformation = trans.multiply(zrot.multiply(yrot).multiply(xrot));
//		Matrix4f rot = zrot.multiply(yrot).multiply(xrot);
//		 transformation = relative.multiply(trans.multiply(interpolation.normalize().toMatrix()));
//		finalRelative = transformation;
//		//System.out.println(finalRelative);
//		
//		if(parent==null) {
//			finalResult =finalRelative;
//		}
//		else {
//			finalResult = parent.finalResult.multiply(finalRelative);
//			
//		}
//	}

}
