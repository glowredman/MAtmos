package eu.ha3.matmos.game.data.modules;

import javax.annotation.concurrent.Immutable;

import eu.ha3.matmos.game.system.MAtmosUtility;

@Deprecated
@Immutable
public final class BlockTriplet {
	private final int nx;
	private final int ny;
	private final int nz;
	
	public BlockTriplet(int nx, int ny, int nz) {
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
	}
	
	public String getBlockRelative(int xx, int yy, int zz, String defaultIfFail) {
		return MAtmosUtility.getNameAt(xx + this.nx, yy + this.ny, zz + this.nz, defaultIfFail);
	}
	
	public String getPowerMetaRelative(int xx, int yy, int zz, String defaultIfFail) {
		return MAtmosUtility.getPowerMetaAt(xx + this.nx, yy + this.ny, zz + this.nz, defaultIfFail);
	}
}
